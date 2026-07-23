package com.ruoyi.asset.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.asset.constant.RedisConstants;
import com.ruoyi.asset.domain.Assets;
import com.ruoyi.asset.domain.Change;
import com.ruoyi.asset.domain.ChangeAttachment;
import com.ruoyi.asset.domain.ChangeDetail;
import com.ruoyi.asset.domain.vo.ChangeVO;
import com.ruoyi.asset.mapper.AssetsMapper;
import com.ruoyi.asset.mapper.ChangeMapper;
import com.ruoyi.asset.service.IChangeAttachmentService;
import com.ruoyi.asset.service.IChangeService;
import com.ruoyi.asset.utils.ChangePdf;
import com.ruoyi.asset.utils.GenerateCode;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.workflow.api.RemoteWorkflowService;
import com.ruoyi.workflow.api.constant.BusinessStatusConstants;
import com.ruoyi.workflow.api.constant.TaskDefinitionConstants;
import com.ruoyi.workflow.api.constant.WorkflowConstants;
import com.ruoyi.workflow.api.domain.CompleteTask;
import com.ruoyi.workflow.api.domain.StartProcess;
import com.ruoyi.workflow.api.domain.vo.CurrentTaskVO;
import com.ruoyi.workflow.api.domain.vo.ProcessStartVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.ruoyi.asset.constant.ThreadPoolExecutorConstants.*;

@Service
public class ChangeServiceImpl extends ServiceImpl<ChangeMapper, Change> implements IChangeService {

    private static final Logger log = LoggerFactory.getLogger(ChangeServiceImpl.class);

    @Resource
    private ChangeMapper changeMapper;

    @Resource
    private AssetsMapper assetsMapper;

    @Resource
    private RemoteWorkflowService remoteWorkflowService;

    @Autowired
    private IChangeAttachmentService changeAttachmentService;

    @Autowired
    private GenerateCode generateCode;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ChangePdf changePdf;

    // ==================== 多线程优化 ====================

    @Override
    public List<ChangeVO> selectChangeList(Change change) {
        return changeMapper.selectChangeList(change);
    }

    @Override
    public Map<String, Integer> countByStatus() {
        try {
            // 并行查询四个状态
            CompletableFuture<Integer> draftFuture = CompletableFuture.supplyAsync(
                    () -> changeMapper.countByBusinessStatus(BusinessStatusConstants.DRAFT),
                    EXECUTOR_SERVICE
            );
            CompletableFuture<Integer> pendingFuture = CompletableFuture.supplyAsync(
                    () -> changeMapper.countByBusinessStatus(BusinessStatusConstants.PENDING),
                    EXECUTOR_SERVICE
            );
            CompletableFuture<Integer> completedFuture = CompletableFuture.supplyAsync(
                    () -> changeMapper.countByBusinessStatus(BusinessStatusConstants.COMPLETED),
                    EXECUTOR_SERVICE
            );
            CompletableFuture<Integer> rejectedFuture = CompletableFuture.supplyAsync(
                    () -> changeMapper.countByBusinessStatus(BusinessStatusConstants.REJECTED),
                    EXECUTOR_SERVICE
            );

            // 等待所有查询完成
            CompletableFuture.allOf(draftFuture, pendingFuture, completedFuture, rejectedFuture).join();

            Map<String, Integer> result = new HashMap<>();
            result.put(BusinessStatusConstants.DRAFT, draftFuture.join() != null ? draftFuture.join() : 0);
            result.put(BusinessStatusConstants.PENDING, pendingFuture.join() != null ? pendingFuture.join() : 0);
            result.put(BusinessStatusConstants.COMPLETED, completedFuture.join() != null ? completedFuture.join() : 0);
            result.put(BusinessStatusConstants.REJECTED, rejectedFuture.join() != null ? rejectedFuture.join() : 0);

            return result;
        } catch (Exception e) {
            log.error("统计审批状态失败", e);
        }
        Map<String, Integer> empty = new HashMap<>();
        empty.put(BusinessStatusConstants.DRAFT, 0);
        empty.put(BusinessStatusConstants.PENDING, 0);
        empty.put(BusinessStatusConstants.COMPLETED, 0);
        empty.put(BusinessStatusConstants.REJECTED, 0);
        return empty;
    }

    @Override
    public ChangeVO selectChangeById(Long id) {
        ChangeVO changeVO = changeMapper.selectChangeById(id);
        if (changeVO == null) {
            return null;
        }

        List<ChangeAttachment> attachments = getAttachmentsByChangeId(id);
        changeVO.setAttachments(attachments);

        List<Assets> assets;
        String businessStatus = changeVO.getBusinessStatus();

        if (BusinessStatusConstants.PENDING.equals(businessStatus)
                || BusinessStatusConstants.REJECTED.equals(businessStatus)) {
            assets = getPendingAssetsFromRedis(id);
            log.debug("【资产变动-详情】审批中或已驳回，从 Redis 读取拟变更数据，变动单 ID：{}，状态：{}",
                    id, businessStatus);
        } else {
            assets = changeMapper.selectAssetsByChangeId(id);
            log.debug("【资产变动-详情】非审批中，从数据库查询资产数据，变动单 ID：{}，状态：{}",
                    id, businessStatus);
        }
        changeVO.setAssets(assets);

        return changeVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertChange(ChangeVO change) {
        log.info("【资产变动-新增】开始，变动类型：{}，资产数量：{}", change.getChangeType(), change.getAssets() != null ? change.getAssets().size() : 0);

        initChangeBasicInfo(change);
        int result = baseMapper.insert(change) > 0 ? 1 : 0;
        log.info("【资产变动-新增】保存变动单成功，ID：{}", change.getId());

        saveAssetRelations(change.getId(), change.getAssets());
        saveAttachmentsAsync(change.getId(), change.getAttachments());

        log.info("【资产变动-新增】完成，变动单ID：{}，编码：{}", change.getId(), change.getChangeCode());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateChange(ChangeVO change) {
        log.info("【资产变动-修改】开始，变动单ID：{}", change.getId());

        Change old = baseMapper.selectById(change.getId());
        if (old == null) {
            log.error("【资产变动-修改】单据不存在，ID：{}", change.getId());
            throw new ServiceException("单据不存在");
        }
        if (StringUtils.isNotEmpty(old.getProcInstId())) {
            log.error("【资产变动-修改】已提交的单据不能修改，ID：{}，流程实例：{}", change.getId(), old.getProcInstId());
            throw new ServiceException("已提交的单据不能修改");
        }

        change.setUpdateBy(SecurityUtils.getUsername());
        change.setUpdateTime(DateUtils.getNowDate());
        int result = baseMapper.updateById(change) > 0 ? 1 : 0;
        log.info("【资产变动-修改】更新变动单成功，ID：{}", change.getId());

        changeMapper.deleteDetailByMasterId(change.getId());
        saveAssetRelations(change.getId(), change.getAssets());
        log.info("【资产变动-修改】更新资产关联成功，数量：{}", change.getAssets() != null ? change.getAssets().size() : 0);

        deleteAttachmentsByChangeId(change.getId());
        saveAttachmentsAsync(change.getId(), change.getAttachments());
        log.info("【资产变动-修改】更新附件成功，数量：{}", change.getAttachments() != null ? change.getAttachments().size() : 0);

        log.info("【资产变动-修改】完成，变动单ID：{}", change.getId());
        return result;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteChangeByIds(Long[] ids) {
        log.info("【资产变动-删除】开始，删除数量：{}", ids.length);

        for (Long id : ids) {
            changeMapper.deleteDetailByMasterId(id);
            deleteAttachmentsByChangeId(id);
            log.debug("【资产变动-删除】清理关联数据，变动单ID：{}", id);
        }
        int result = changeMapper.deleteChangeByIds(ids);
        log.info("【资产变动-删除】完成，删除数量：{}", result);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveDraft(ChangeVO change) {
        log.info("【资产变动-暂存】开始，变动类型：{}，资产数量：{}", change.getChangeType(), change.getAssets() != null ? change.getAssets().size() : 0);

        initChangeBasicInfo(change);

        baseMapper.insert(change);
        log.info("【资产变动-暂存】保存变动单成功，ID：{}", change.getId());

        saveAssetRelations(change.getId(), change.getAssets());
        saveAttachmentsAsync(change.getId(), change.getAttachments());

        log.info("【资产变动-暂存】完成，变动单ID：{}，编码：{}", change.getId(), change.getChangeCode());
        return change.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitChange(ChangeVO change) {
        log.info("【资产变动-提交】开始处理，变动单ID：{}", change.getId());
        Long changeId = change.getId();
        if (!Objects.equals(SecurityUtils.getUserId(), change.getApplicantId())) {
            throw new ServiceException("无权操作他人申请单据");
        }

        if (changeId == null) {
            log.info("【资产变动-提交】步骤1：新建变动单");
            initChangeBasicInfo(change);
            baseMapper.insert(change);
            changeId = change.getId();
            saveAssetRelations(changeId, change.getAssets());
            saveAttachmentsAsync(changeId, change.getAttachments());
            log.info("【资产变动-提交】步骤1完成，新建变动单成功，ID：{}，编码：{}", changeId, change.getChangeCode());
        } else {
            log.info("【资产变动-提交】步骤1：更新已有变动单，ID：{}", changeId);
            Change existing = baseMapper.selectById(changeId);
            if (existing == null) {
                throw new ServiceException("单据不存在");
            }

            String procInstId = existing.getProcInstId();
            if (StringUtils.isNotEmpty(procInstId)) {
                log.info("【资产变动-提交】检测到已有流程实例：{}", procInstId);
                R<Boolean> endedResult = remoteWorkflowService.isProcessEnded(procInstId);
                if (endedResult.getData() != null && !endedResult.getData()) {
                    log.info("【资产变动-提交】流程未结束，检查当前任务节点");
                    R<CurrentTaskVO> taskResult = remoteWorkflowService.getCurrentTask(procInstId);
                    if (taskResult != null && taskResult.getCode() == R.SUCCESS && taskResult.getData() != null) {
                        String currentTaskKey = taskResult.getData().getTaskDefinitionKey();
                        log.info("【资产变动-提交】当前任务节点：{}", currentTaskKey);

                        if (TaskDefinitionConstants.SUBMIT.equals(currentTaskKey)) {
                            log.info("【资产变动-提交】检测到驳回后重新提交，节点：{}", currentTaskKey);
                            updateChangeBusinessData(existing, change, changeId);
                            log.info("【资产变动-提交】业务数据更新完成");

                            CompleteTask completeDTO = new CompleteTask();
                            completeDTO.setTaskId(taskResult.getData().getTaskId());
                            completeDTO.setApproved(true);
                            completeDTO.setComment("资产经办人重新提交");

                            R<Void> completeResult = remoteWorkflowService.completeTask(completeDTO);
                            if (completeResult == null || completeResult.getCode() != R.SUCCESS) {
                                log.error("【资产变动-提交】重新提交失败，流程实例：{}，错误：{}", procInstId, completeResult != null ? completeResult.getMsg() : "未知错误");
                                throw new ServiceException("重新提交失败：" + (completeResult != null ? completeResult.getMsg() : "未知错误"));
                            }

                            Change updateChange = new Change();
                            updateChange.setId(changeId);
                            updateChange.setBusinessStatus(BusinessStatusConstants.PENDING);
                            baseMapper.updateById(updateChange);
                            log.info("【资产变动-提交】驳回后重新提交成功，流程实例：{}，流程流转到：managerApprove", procInstId);
                            return changeId;
                        } else {
                            log.error("【资产变动-提交】流程未结束，但不在提交节点，当前节点：{}", currentTaskKey);
                        }
                    }
                    throw new ServiceException("该单据已有进行中的审批流程，且不在提交节点");
                }
                log.info("【资产变动-提交】流程已结束，删除旧流程实例：{}", procInstId);
                remoteWorkflowService.deleteProcessInstance(procInstId, "重新提交");
            }

            updateChangeBusinessData(existing, change, changeId);
            log.info("【资产变动-提交】步骤1完成，更新变动单成功，ID：{}", changeId);
        }

        log.info("【资产变动-提交】步骤2：首次提交，启动工作流");
        Change existingChange = baseMapper.selectById(changeId);

        StartProcess startDTO = new StartProcess();
        startDTO.setProcessKey(WorkflowConstants.PROCESS_KEY_ASSET_CHANGE);
        startDTO.setBusinessKey(String.valueOf(changeId));

        Map<String, Object> variables = new HashMap<>();
        variables.put("applicantId", existingChange.getApplicantId());
        startDTO.setVariables(variables);
        log.info("【资产变动-提交】流程参数：processKey={}, businessKey={}, applicantId={}", startDTO.getProcessKey(), startDTO.getBusinessKey(), variables.get("applicantId"));

        R<ProcessStartVO> result = remoteWorkflowService.startProcess(startDTO);
        if (result == null || result.getCode() != R.SUCCESS || result.getData() == null) {
            log.error("【资产变动-提交】启动流程失败，变动单ID：{}", changeId);
            throw new ServiceException("启动流程失败");
        }
        log.info("【资产变动-提交】流程启动成功，流程实例ID：{}", result.getData().getProcInstId());

        log.info("【资产变动-提交】步骤3：自动完成submit任务");
        String procInstId = result.getData().getProcInstId();
        R<CurrentTaskVO> currentTaskResult = remoteWorkflowService.getCurrentTask(procInstId);
        if (currentTaskResult != null && currentTaskResult.getCode() == R.SUCCESS && currentTaskResult.getData() != null) {
            CurrentTaskVO currentTask = currentTaskResult.getData();
            log.info("【资产变动-提交】当前任务：taskId={}, taskName={}, taskDefinitionKey={}", currentTask.getTaskId(), currentTask.getTaskName(), currentTask.getTaskDefinitionKey());

            if (TaskDefinitionConstants.SUBMIT.equals(currentTask.getTaskDefinitionKey())) {
                CompleteTask completeDTO = new CompleteTask();
                completeDTO.setTaskId(currentTask.getTaskId());
                completeDTO.setApproved(true);
                completeDTO.setComment(null);

                R<Void> completeResult = remoteWorkflowService.completeTask(completeDTO);
                if (completeResult == null || completeResult.getCode() != R.SUCCESS) {
                    log.warn("【资产变动-提交】自动完成submit任务失败，流程实例：{}，错误：{}", procInstId, completeResult != null ? completeResult.getMsg() : "未知错误");
                } else {
                    log.info("【资产变动-提交】自动完成submit任务成功，流程流转到下一节点");
                }
            } else {
                log.warn("【资产变动-提交】当前任务不是submit节点，跳过自动完成，当前节点：{}", currentTask.getTaskDefinitionKey());
            }
        } else {
            log.warn("【资产变动-提交】获取当前任务失败，流程实例：{}", procInstId);
        }

        Change updateChange = new Change();
        updateChange.setId(changeId);
        updateChange.setProcInstId(procInstId);
        updateChange.setBusinessStatus(BusinessStatusConstants.PENDING);
        log.info("【资产变动-提交】步骤4：保存流程信息，变动单ID：{}，流程实例ID：{}，业务状态：{}", changeId, procInstId, BusinessStatusConstants.PENDING);
        baseMapper.updateById(updateChange);

        if (change.getAssets() != null && !change.getAssets().isEmpty()) {
            try {
                String redisKey = RedisConstants.ASSET_CHANGE_DRAFT_PREFIX + changeId;
                String assetsJson = JSONUtil.toJsonStr(change.getAssets());
                stringRedisTemplate.opsForValue().set(redisKey, assetsJson, RedisConstants.ASSET_CHANGE_DRAFT_TTL, TimeUnit.DAYS);
                log.info("【资产变动-提交】步骤5：保存拟变更资产数据到Redis，变动单ID：{}，资产数量：{}，缓存key：{}", changeId, change.getAssets().size(), redisKey);
            } catch (Exception e) {
                log.error("【资产变动-提交】保存拟变更资产数据到Redis失败，变动单ID：{}", changeId, e);
            }
        }

        log.info("【资产变动-提交】全部完成，变动单ID：{}，流程实例ID：{}", changeId, procInstId);
        return changeId;
    }

    private void initChangeBasicInfo(ChangeVO change) {
        if (StringUtils.isEmpty(change.getChangeCode())) {
            change.setChangeCode(generateCode.generateCode("ZCBD"));
        }
        if (change.getApplicantId() == null) {
            change.setApplicantId(SecurityUtils.getUserId());
        }
        if (change.getApplyDate() == null) {
            change.setApplyDate(DateUtils.getNowDate());
        }
        change.setCreateBy(SecurityUtils.getUsername());
        change.setCreateTime(DateUtils.getNowDate());
    }

    private void updateChangeBusinessData(Change existing, ChangeVO change, Long changeId) {
        log.info("【资产变动-业务数据更新】开始，变动单ID：{}", changeId);

        existing.setChangeType(change.getChangeType());
        existing.setChangeReason(change.getChangeReason());
        existing.setRemark(change.getRemark());
        existing.setUpdateBy(SecurityUtils.getUsername());
        baseMapper.updateById(existing);
        log.info("【资产变动-业务数据更新】更新主表成功");

        changeMapper.deleteDetailByMasterId(changeId);
        saveAssetRelations(changeId, change.getAssets());

        deleteAttachmentsByChangeId(changeId);
        saveAttachmentsAsync(changeId, change.getAttachments());

        log.info("【资产变动-业务数据更新】完成");
    }

    /**
     * 审批资产变动单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int approveChange(Long id, boolean result, String comment, Long approverId) {
        log.info("【资产变动-审批】开始，变动单ID：{}，审批结果：{}，审批人：{}，审批意见：{}",
                id, result ? "通过" : "驳回", approverId, comment);
        // 1.根据ID 查询资产变动单
        Change change = baseMapper.selectById(id);

        if (change == null) {
            throw new ServiceException("单据不存在");
        }
        if (StringUtils.isEmpty(change.getProcInstId())) {
            throw new ServiceException("未提交申请单据");
        }
        if (Objects.equals(SecurityUtils.getUserId(), change.getApplicantId())) {
            throw new ServiceException("不能审批本人提交的申请");
        }

        // 2.获取当前任务
        R<CurrentTaskVO> taskResult = remoteWorkflowService.getCurrentTask(change.getProcInstId());
        if (taskResult.getCode() != R.SUCCESS || taskResult.getData() == null) {

            if (isProcessEnded(change.getProcInstId())) {
                throw new ServiceException("流程已结束,无法进行审批");
            }
            throw new ServiceException("未找到待办任务");
        }

        CurrentTaskVO currentTask = taskResult.getData();
        log.info("【资产变动-审批】当前任务信息：taskId={}, taskName={}, taskDefinitionKey={}, assignee={}",
                currentTask.getTaskId(), currentTask.getTaskName(), currentTask.getTaskDefinitionKey(), currentTask.getAssignee());

        log.info("【资产变动-审批】开始完成工作流任务，taskId：{}", currentTask.getTaskId());
        CompleteTask completeDTO = new CompleteTask();
        completeDTO.setTaskId(currentTask.getTaskId());
        completeDTO.setApproved(result);
        completeDTO.setComment(comment);

        Map<String, Object> variables = new HashMap<>();
        variables.put("approverId", approverId);
        completeDTO.setVariables(variables);
        log.info("【资产变动-审批】流程变量：approverId={}", approverId);

        R<Void> completeResult = remoteWorkflowService.completeTask(completeDTO);
        if (completeResult == null || completeResult.getCode() != R.SUCCESS) {
            log.error("【资产变动-审批】办理任务失败，taskId：{}，错误：{}",
                    currentTask.getTaskId(), completeResult != null ? completeResult.getMsg() : "未知错误");
            throw new ServiceException("办理任务失败");
        }
        log.info("【资产变动-审批】工作流任务完成成功");

        if (result) {
            log.info("【资产变动-审批】审批通过，执行资产变更，变动单ID：{}", id);
            executeChangeAsync(id);
            log.info("【资产变动-审批】资产变更执行成功");
        } else {
            log.info("【资产变动-审批】审批驳回，不执行资产变更，流程回到 submit 节点");
        }

        log.info("【资产变动-审批】准备更新业务状态，当前业务状态：{}", change.getBusinessStatus());
        updateBusinessStatusAfterApproval(change);
        log.info("【资产变动-审批】业务状态更新完成");

        log.info("【资产变动-审批】审批完成，变动单ID：{}，审批结果：{}", id, result ? "通过" : "驳回");
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int withdrawChange(Long id) {
        log.info("【资产变动-撤回】开始，变动单ID：{}", id);

        Change change = baseMapper.selectById(id);
        if (change == null) {
            log.error("【资产变动-撤回】单据不存在，ID：{}", id);
            throw new ServiceException("单据不存在");
        }

        if (StringUtils.isEmpty(change.getProcInstId())) {
            log.error("【资产变动-撤回】流程未启动，变动单ID：{}", id);
            throw new ServiceException("流程未启动");
        }

        if (isProcessEnded(change.getProcInstId())) {
            log.error("【资产变动-撤回】流程已结束，无法撤回，流程实例ID：{}", change.getProcInstId());
            throw new ServiceException("流程已结束，无法撤回");
        }

        if (!Objects.equals(SecurityUtils.getUserId(), change.getApplicantId())) {
            throw new ServiceException("无权操作他人申请单据");
        }

        log.info("【资产变动-撤回】删除流程实例，流程实例ID：{}，原因：申请人撤回", change.getProcInstId());
        remoteWorkflowService.deleteProcessInstance(change.getProcInstId(), "申请人撤回");

        Change updateChange = new Change();
        updateChange.setId(id);
        updateChange.setProcInstId(null);
        updateChange.setBusinessStatus(BusinessStatusConstants.DRAFT);
        baseMapper.updateById(updateChange);

        log.info("【资产变动-撤回】撤回成功，变动单ID：{}，业务状态设置为DRAFT", id);
        return 1;
    }

    @Override
    public void exportPdf(HttpServletResponse response, Long id) throws Exception {
        // 异步查询变动单数据
        CompletableFuture<ChangeVO> future = CompletableFuture.supplyAsync(
                () -> selectChangeById(id),
                EXECUTOR_SERVICE
        );

        try {
            ChangeVO change = future.get(30, TimeUnit.SECONDS);
            if (change == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "变动单不存在");
                return;
            }
            // 同步生成PDF（PDF生成本身是IO密集型，写入响应流必须同步）
            changePdf.exportPdf(response, change);
        } catch (Exception e) {
            log.error("【资产变动-导出PDF】失败，变动单ID：{}", id, e);
            throw new ServiceException("导出PDF失败：" + e.getMessage());
        }
    }

    // ==================== 多线程优化核心方法 ====================

    /**
     * 异步并行保存附件（IO密集型）
     */
    private void saveAttachmentsAsync(Long changeId, List<ChangeAttachment> attachments) {
        if (attachments == null || attachments.isEmpty()) {
            log.debug("【资产变动-附件】没有附件需要保存，变动单ID：{}", changeId);
            return;
        }

        // 前置处理：设置公共字段
        attachments.forEach(attachment -> {
            attachment.setMasterId(changeId);
            attachment.setUploadBy(SecurityUtils.getUsername());
            attachment.setUploadTime(DateUtils.getNowDate());
        });

        // 并行保存附件
        List<CompletableFuture<Boolean>> futures = attachments.stream()
                .map(attachment -> CompletableFuture.supplyAsync(
                        () -> changeAttachmentService.save(attachment),
                        EXECUTOR_SERVICE
                ))
                .collect(Collectors.toList());

        // 等待所有保存完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        long successCount = futures.stream().filter(CompletableFuture::join).count();
        log.info("【资产变动-附件】并行保存完成，变动单ID：{}，成功：{}，总数：{}",
                changeId, successCount, attachments.size());
    }

    /**
     * 异步并行执行资产变更（IO密集型，批量更新资产）
     */
    @Transactional(rollbackFor = Exception.class)
    void executeChangeAsync(Long changeId) {
        log.info("【资产变动-执行】开始执行资产变更，变动单ID：{}", changeId);

        String redisKey = RedisConstants.ASSET_CHANGE_DRAFT_PREFIX + changeId;
        String assetsJson = stringRedisTemplate.opsForValue().get(redisKey);

        if (StringUtils.isEmpty(assetsJson)) {
            log.error("【资产变动-执行】未找到拟变更资产数据，变动单ID：{}，Redis key：{}", changeId, redisKey);
            throw new ServiceException("未找到拟变更资产数据，请重新提交申请");
        }

        List<Assets> pendingAssets;
        try {
            pendingAssets = JSONUtil.toList(JSONUtil.parseArray(assetsJson), Assets.class);
            log.info("【资产变动-执行】从 Redis 读取拟变更资产数据成功，变动单ID：{}，资产数量：{}", changeId, pendingAssets.size());
        } catch (Exception e) {
            log.error("【资产变动-执行】解析拟变更资产数据失败，变动单ID：{}", changeId, e);
            throw new ServiceException("解析拟变更资产数据失败");
        }

        if (pendingAssets == null || pendingAssets.isEmpty()) {
            log.warn("【资产变动-执行】没有资产需要变更");
            return;
        }

        // 设置公共字段
        pendingAssets.forEach(asset -> asset.setUpdateBy(SecurityUtils.getUsername()));

        // 并行更新资产
        List<CompletableFuture<Boolean>> futures = pendingAssets.stream()
                .map(asset -> CompletableFuture.supplyAsync(
                        () -> assetsMapper.updateById(asset) > 0,
                        EXECUTOR_SERVICE
                ))
                .collect(Collectors.toList());

        // 等待所有更新完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        long successCount = futures.stream().filter(CompletableFuture::join).count();
        long failCount = pendingAssets.size() - successCount;

        log.info("【资产变动-执行】资产变更执行完成，变动单ID：{}，成功：{}，失败：{}",
                changeId, successCount, failCount);

        if (failCount > 0) {
            throw new ServiceException(String.format("资产变更部分失败，成功：%d，失败：%d", successCount, failCount));
        }

        // 清理Redis缓存
        stringRedisTemplate.delete(redisKey);
        log.info("【资产变动-执行】清理 Redis 缓存成功，key：{}", redisKey);
    }

    // ==================== 原有辅助方法（保持不变） ====================

    private void saveAssetRelations(Long changeId, List<Assets> assets) {
        if (assets == null || assets.isEmpty()) {
            log.debug("【资产变动-资产关联】没有资产关联需要保存，变动单ID：{}", changeId);
            return;
        }

        List<ChangeDetail> detailList = new ArrayList<>();
        for (Assets asset : assets) {
            ChangeDetail detail = new ChangeDetail();
            detail.setMasterId(changeId);
            detail.setAssetId(asset.getId());
            detailList.add(detail);
        }
        changeMapper.batchInsertDetail(detailList);
        log.info("【资产变动-资产关联】保存成功，变动单ID：{}，资产数量：{}", changeId, detailList.size());
    }

    private List<ChangeAttachment> getAttachmentsByChangeId(Long changeId) {
        ChangeAttachment query = new ChangeAttachment();
        query.setMasterId(changeId);
        return changeAttachmentService.selectChangeAttachmentList(query);
    }

    private void deleteAttachmentsByChangeId(Long changeId) {
        changeAttachmentService.deleteByMasterId(changeId);
    }

    private boolean isProcessEnded(String procInstId) {
        try {
            R<Boolean> result = remoteWorkflowService.isProcessEnded(procInstId);
            return result != null && result.getCode() == R.SUCCESS && Boolean.TRUE.equals(result.getData());
        } catch (Exception e) {
            log.error("【资产变动-检查流程】检查流程是否结束失败，procInstId={}", procInstId, e);
            return false;
        }
    }

    private void updateBusinessStatusAfterApproval(Change change) {
        if (change == null || StringUtils.isEmpty(change.getProcInstId())) {
            log.warn("【资产变动-状态更新】change 或 procInstId 为空，跳过状态更新");
            return;
        }

        try {
            log.info("【资产变动-状态更新】开始更新业务状态，变动单ID：{}，流程实例ID：{}",
                    change.getId(), change.getProcInstId());

            Change updateChange = new Change();
            updateChange.setId(change.getId());

            R<Boolean> endedResult = remoteWorkflowService.isProcessEnded(change.getProcInstId());
            if (endedResult != null && endedResult.getCode() == R.SUCCESS && Boolean.TRUE.equals(endedResult.getData())) {
                log.info("【资产变动-状态更新】流程已结束，设置业务状态为 COMPLETED");
                updateChange.setBusinessStatus(BusinessStatusConstants.COMPLETED);
                baseMapper.updateById(updateChange);
                log.info("【资产变动-状态更新】业务状态更新为 COMPLETED 成功");
                return;
            }

            R<CurrentTaskVO> taskResult = remoteWorkflowService.getCurrentTask(change.getProcInstId());
            if (taskResult != null && taskResult.getCode() == R.SUCCESS && taskResult.getData() != null) {
                String currentTaskKey = taskResult.getData().getTaskDefinitionKey();
                log.info("【资产变动-状态更新】当前任务节点：{}", currentTaskKey);

                if (TaskDefinitionConstants.SUBMIT.equals(currentTaskKey)) {
                    log.info("【资产变动-状态更新】审批驳回，流程回到 submit 节点，设置业务状态为 REJECTED");
                    updateChange.setBusinessStatus(BusinessStatusConstants.REJECTED);
                    baseMapper.updateById(updateChange);
                    log.info("【资产变动-状态更新】业务状态更新为 REJECTED 成功");
                    return;
                } else {
                    log.info("【资产变动-状态更新】流程未结束，当前节点：{}，业务状态保持 PENDING", currentTaskKey);
                    updateChange.setBusinessStatus(BusinessStatusConstants.PENDING);
                    baseMapper.updateById(updateChange);
                    log.info("【资产变动-状态更新】业务状态更新为 PENDING 成功");
                    return;
                }
            }

            log.warn("【资产变动-状态更新】无法获取任务状态，暂不更新业务状态");
        } catch (Exception e) {
            log.error("【资产变动-状态更新】更新业务状态失败，变动单ID：{}", change.getId(), e);
        }
    }

    private List<Assets> getPendingAssetsFromRedis(Long changeId) {
        try {
            String redisKey = RedisConstants.ASSET_CHANGE_DRAFT_PREFIX + changeId;
            String assetsJson = stringRedisTemplate.opsForValue().get(redisKey);

            if (StringUtils.isEmpty(assetsJson)) {
                log.warn("【资产变动-Redis】未找到拟变更数据，变动单 ID：{}，Redis key：{}", changeId, redisKey);
                return new ArrayList<>();
            }

            List<Assets> assets = JSONUtil.toList(JSONUtil.parseArray(assetsJson), Assets.class);
            log.info("【资产变动-Redis】读取拟变更数据成功，变动单 ID：{}，资产数量：{}", changeId, assets.size());
            return assets;
        } catch (Exception e) {
            log.error("【资产变动-Redis】读取拟变更数据失败，变动单 ID：{}", changeId, e);
            return new ArrayList<>();
        }
    }
}