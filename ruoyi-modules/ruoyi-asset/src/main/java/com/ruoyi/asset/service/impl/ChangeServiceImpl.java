package com.ruoyi.asset.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.ruoyi.asset.constant.RedisConstants;
import com.ruoyi.asset.domain.Assets;
import com.ruoyi.asset.domain.Change;
import com.ruoyi.asset.domain.ChangeAttachment;
import com.ruoyi.asset.domain.ChangeDetail;
import com.ruoyi.asset.domain.vo.ChangeVO;
import com.ruoyi.asset.mapper.ChangeMapper;
import com.ruoyi.asset.service.IAssetsService;
import com.ruoyi.asset.service.IChangeAttachmentService;
import com.ruoyi.asset.service.IChangeService;
import com.ruoyi.asset.utils.ChangePdf;
import com.ruoyi.asset.utils.GenerateCode;
import com.ruoyi.asset.utils.RetryUtils;
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

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.ruoyi.asset.constant.ThreadPoolExecutorConstants.BIZ_EXECUTOR;
import static com.ruoyi.asset.constant.ThreadPoolExecutorConstants.IO_EXECUTOR;

/**
 * 资产变动单Service实现类
 * <p>
 * 负责资产变动申请、审批、资产变更执行等核心业务逻辑处理。
 * 集成工作流引擎实现审批流程管理，使用Redis缓存拟变更资产数据，
 * 采用多线程异步处理和重试机制提升系统性能和稳定性。
 * </p>
 *
 * <p><b>整体优化说明：</b></p>
 * <ul>
 *   <li><b>线程池隔离：</b>业务操作使用 BIZ_EXECUTOR，IO操作使用 IO_EXECUTOR</li>
 *   <li><b>失败重试：</b>数据库批量操作和Redis操作均带Guava Retry重试机制</li>
 *   <li><b>批量更新：</b>审批通过后将逐条更新改为Guava分段批量更新，每批100条</li>
 *   <li><b>Redis降级：</b>Redis异常或无数据时自动降级到数据库直查</li>
 * </ul>
 *
 * @author wangqin
 * @date 2026-08-21
 */
@Service
public class ChangeServiceImpl extends ServiceImpl<ChangeMapper, Change> implements IChangeService {

    private static final Logger log = LoggerFactory.getLogger(ChangeServiceImpl.class);

    @Autowired
    private ChangeMapper changeMapper;

    @Autowired
    private IAssetsService assetsService;

    @Autowired
    private RemoteWorkflowService remoteWorkflowService;

    @Autowired
    private IChangeAttachmentService changeAttachmentService;

    @Autowired
    private GenerateCode generateCode;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ChangePdf changePdf;

    /**
     * 查询资产变动列表
     * <p>
     * 支持多条件组合查询，返回包含变动主信息和关联资产的视图对象。
     * 查询条件包括：变动类型、业务状态、申请人、申请时间范围等。
     * </p>
     *
     * @param change 查询条件对象，包含筛选字段
     * @return 变动视图对象列表，按创建时间降序排列
     */
    @Override
    public List<ChangeVO> selectChangeList(Change change) {
        return changeMapper.selectChangeList(change);
    }

    /**
     * 并行查询四个审批状态的数量统计
     * <p>
     * 【优化点】使用 BIZ_EXECUTOR 线程池并行查询四个状态的记录数，
     * 显著提升统计接口响应速度。
     * </p>
     * <p>
     * 统计的状态包括：草稿(DRAFT)、审批中(PENDING)、已完成(COMPLETED)、已驳回(REJECTED)
     * </p>
     *
     * @return 状态编码到数量的映射Map，包含所有四个状态的计数；异常时返回全0的Map
     */
    @Override
    public Map<String, Integer> countByStatus() {
        try {
            // 使用 BIZ_EXECUTOR 线程池并行查询四个状态
            CompletableFuture<Integer> draftFuture = CompletableFuture.supplyAsync(
                    () -> changeMapper.countByBusinessStatus(BusinessStatusConstants.DRAFT),
                    BIZ_EXECUTOR
            );
            CompletableFuture<Integer> pendingFuture = CompletableFuture.supplyAsync(
                    () -> changeMapper.countByBusinessStatus(BusinessStatusConstants.PENDING),
                    BIZ_EXECUTOR
            );
            CompletableFuture<Integer> completedFuture = CompletableFuture.supplyAsync(
                    () -> changeMapper.countByBusinessStatus(BusinessStatusConstants.COMPLETED),
                    BIZ_EXECUTOR
            );
            CompletableFuture<Integer> rejectedFuture = CompletableFuture.supplyAsync(
                    () -> changeMapper.countByBusinessStatus(BusinessStatusConstants.REJECTED),
                    BIZ_EXECUTOR
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
            log.error("【资产变动-统计】统计审批状态失败", e);
        }
        Map<String, Integer> empty = new HashMap<>();
        empty.put(BusinessStatusConstants.DRAFT, 0);
        empty.put(BusinessStatusConstants.PENDING, 0);
        empty.put(BusinessStatusConstants.COMPLETED, 0);
        empty.put(BusinessStatusConstants.REJECTED, 0);
        return empty;
    }

    /**
     * 根据ID查询变动单详情
     * <p>
     * 【优化点】审批中/已驳回状态优先从Redis读取拟变更资产数据，
     * Redis异常时自动降级到数据库查询。
     * </p>
     * <p>
     * 查询流程：
     * <ol>
     *   <li>从数据库查询变动主信息和审批状态</li>
     *   <li>查询关联的附件列表</li>
     *   <li>根据业务状态决定资产数据来源：审批中/已驳回从Redis读取，其他状态从数据库查询</li>
     * </ol>
     * </p>
     *
     * @param id 变动记录主键ID
     * @return 变动视图对象，包含主信息、附件列表和资产列表；不存在时返回null
     */
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
            // 从 Redis 读取（带降级）
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

    /**
     * 新增变动单
     * <p>
     * 事务操作，包含：
     * <ol>
     *   <li>初始化变动单基本信息（编码、申请人、申请时间等）</li>
     *   <li>保存变动主表记录</li>
     *   <li>保存资产关联关系（变动明细）</li>
     *   <li>异步保存附件信息</li>
     * </ol>
     * </p>
     *
     * @param change 变动视图对象，包含主信息和关联数据
     * @return 影响行数，成功返回1，失败返回0
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertChange(ChangeVO change) {
        log.info("【资产变动-新增】开始，变动类型：{}，资产数量：{}",
                change.getChangeType(), change.getAssets() != null ? change.getAssets().size() : 0);

        initChangeBasicInfo(change);
        int result = changeMapper.insert(change) > 0 ? 1 : 0;
        log.info("【资产变动-新增】保存变动单成功，ID：{}", change.getId());

        saveAssetRelations(change.getId(), change.getAssets());
        saveAttachmentsAsync(change.getId(), change.getAttachments());

        log.info("【资产变动-新增】完成，变动单ID：{}，编码：{}", change.getId(), change.getChangeCode());
        return result;
    }

    /**
     * 修改变动单
     * <p>
     * 事务操作，执行前校验：
     * <ul>
     *   <li>单据必须存在</li>
     *   <li>已提交的（有流程实例ID）单据不可修改</li>
     * </ul>
     * 修改流程：
     * <ol>
     *   <li>更新变动主表</li>
     *   <li>删除旧的资产关联并重新保存</li>
     *   <li>删除旧的附件并异步保存新附件</li>
     * </ol>
     * </p>
     *
     * @param change 变动视图对象，必须包含ID
     * @return 影响行数，成功返回1
     * @throws ServiceException 单据不存在或已提交时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateChange(ChangeVO change) {
        log.info("【资产变动-修改】开始，变动单ID：{}", change.getId());

        Change old = changeMapper.selectById(change.getId());
        if (old == null) {
            log.error("【资产变动-修改】单据不存在，ID：{}", change.getId());
            throw new ServiceException("单据不存在");
        }
        if (StringUtils.isNotEmpty(old.getProcInstId())) {
            log.error("【资产变动-修改】已提交的单据不能修改，ID：{}，流程实例：{}",
                    change.getId(), old.getProcInstId());
            throw new ServiceException("已提交的单据不能修改");
        }

        change.setUpdateBy(SecurityUtils.getUsername());
        change.setUpdateTime(DateUtils.getNowDate());
        int result = changeMapper.updateById(change) > 0 ? 1 : 0;
        log.info("【资产变动-修改】更新变动单成功，ID：{}", change.getId());

        changeMapper.deleteDetailByMasterId(change.getId());
        saveAssetRelations(change.getId(), change.getAssets());
        log.info("【资产变动-修改】更新资产关联成功，数量：{}",
                change.getAssets() != null ? change.getAssets().size() : 0);

        deleteAttachmentsByChangeId(change.getId());
        saveAttachmentsAsync(change.getId(), change.getAttachments());
        log.info("【资产变动-修改】更新附件成功，数量：{}",
                change.getAttachments() != null ? change.getAttachments().size() : 0);

        log.info("【资产变动-修改】完成，变动单ID：{}", change.getId());
        return result;
    }

    /**
     * 批量删除变动单
     * <p>
     * 事务操作，级联删除关联数据：
     * <ol>
     *   <li>删除各变动单的资产关联明细</li>
     *   <li>删除各变动单的附件记录</li>
     *   <li>批量删除变动主表记录</li>
     * </ol>
     * </p>
     *
     * @param ids 待删除的变动记录主键ID数组
     * @return 实际删除的主记录条数
     */
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

    /**
     * 暂存变动单
     * <p>
     * 将变动单保存为草稿状态，不启动工作流审批流程。
     * 后续可继续编辑或提交启动审批。
     * </p>
     *
     * @param change 变动视图对象
     * @return 保存后的变动单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveDraft(ChangeVO change) {
        log.info("【资产变动-暂存】开始，变动类型：{}，资产数量：{}",
                change.getChangeType(), change.getAssets() != null ? change.getAssets().size() : 0);

        initChangeBasicInfo(change);
        changeMapper.insert(change);
        log.info("【资产变动-暂存】保存变动单成功，ID：{}", change.getId());

        saveAssetRelations(change.getId(), change.getAssets());
        saveAttachmentsAsync(change.getId(), change.getAttachments());

        log.info("【资产变动-暂存】完成，变动单ID：{}，编码：{}", change.getId(), change.getChangeCode());
        return change.getId();
    }

    /**
     * 提交变动单（启动工作流 + Redis缓存）
     * <p>
     * 核心方法，处理三种场景：
     * <ul>
     *   <li><b>首次提交：</b>新建变动单 → 启动工作流 → 自动完成submit任务 → 缓存数据到Redis</li>
     *   <li><b>驳回后重新提交：</b>更新业务数据 → 完成审批任务 → 流程流转到下一节点</li>
     *   <li><b>流程已结束重新提交：</b>删除旧流程 → 更新数据 → 重新启动新流程</li>
     * </ul>
     * </p>
     *
     * @param change 变动视图对象
     * @return 变动单ID（新建时返回生成的ID，已有时返回原ID）
     * @throws ServiceException 无权操作、单据不存在、流程异常时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitChange(ChangeVO change) {
        log.info("【资产变动-提交】开始处理，变动单ID：{}", change.getId());
        Long changeId = change.getId();

        if (changeId == null) {
            log.info("【资产变动-提交】步骤1：新建变动单");
            initChangeBasicInfo(change);
            changeMapper.insert(change);
            changeId = change.getId();
            saveAssetRelations(changeId, change.getAssets());
            saveAttachmentsAsync(changeId, change.getAttachments());
            log.info("【资产变动-提交】步骤1完成，新建变动单成功，ID：{}，编码：{}", changeId, change.getChangeCode());
        } else {
            log.info("【资产变动-提交】步骤1：更新已有变动单，ID：{}", changeId);
            Change existing = changeMapper.selectById(changeId);
            if (existing == null) {
                throw new ServiceException("单据不存在");
            }

            // 校验操作权限：仅允许申请人本人重新提交自己的单据
            if (!Objects.equals(SecurityUtils.getUserId(), existing.getApplicantId())) {
                throw new ServiceException("无权操作他人申请单据");
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
                                log.error("【资产变动-提交】重新提交失败，流程实例：{}，错误：{}",
                                        procInstId, completeResult != null ? completeResult.getMsg() : "未知错误");
                                throw new ServiceException("重新提交失败：" +
                                        (completeResult != null ? completeResult.getMsg() : "未知错误"));
                            }

                            Change updateChange = new Change();
                            updateChange.setId(changeId);
                            updateChange.setBusinessStatus(BusinessStatusConstants.PENDING);
                            changeMapper.updateById(updateChange);
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

        // 启动工作流
        log.info("【资产变动-提交】步骤2：首次提交，启动工作流");
        Change existingChange = changeMapper.selectById(changeId);

        StartProcess startDTO = new StartProcess();
        startDTO.setProcessKey(WorkflowConstants.PROCESS_KEY_ASSET_CHANGE);
        startDTO.setBusinessKey(String.valueOf(changeId));

        Map<String, Object> variables = new HashMap<>();
        variables.put("applicantId", existingChange.getApplicantId());
        startDTO.setVariables(variables);
        log.info("【资产变动-提交】流程参数：processKey={}, businessKey={}, applicantId={}",
                startDTO.getProcessKey(), startDTO.getBusinessKey(), variables.get("applicantId"));

        R<ProcessStartVO> result = remoteWorkflowService.startProcess(startDTO);
        if (result == null || result.getCode() != R.SUCCESS || result.getData() == null) {
            log.error("【资产变动-提交】启动流程失败，变动单ID：{}", changeId);
            throw new ServiceException("启动流程失败");
        }
        log.info("【资产变动-提交】流程启动成功，流程实例ID：{}", result.getData().getProcInstId());

        // 自动完成 submit 任务
        log.info("【资产变动-提交】步骤3：自动完成submit任务");
        String procInstId = result.getData().getProcInstId();
        R<CurrentTaskVO> currentTaskResult = remoteWorkflowService.getCurrentTask(procInstId);
        if (currentTaskResult != null && currentTaskResult.getCode() == R.SUCCESS && currentTaskResult.getData() != null) {
            CurrentTaskVO currentTask = currentTaskResult.getData();
            log.info("【资产变动-提交】当前任务：taskId={}, taskName={}, taskDefinitionKey={}",
                    currentTask.getTaskId(), currentTask.getTaskName(), currentTask.getTaskDefinitionKey());

            if (TaskDefinitionConstants.SUBMIT.equals(currentTask.getTaskDefinitionKey())) {
                CompleteTask completeDTO = new CompleteTask();
                completeDTO.setTaskId(currentTask.getTaskId());
                completeDTO.setApproved(true);
                completeDTO.setComment(null);

                R<Void> completeResult = remoteWorkflowService.completeTask(completeDTO);
                if (completeResult == null || completeResult.getCode() != R.SUCCESS) {
                    log.warn("【资产变动-提交】自动完成submit任务失败，流程实例：{}，错误：{}",
                            procInstId, completeResult != null ? completeResult.getMsg() : "未知错误");
                } else {
                    log.info("【资产变动-提交】自动完成submit任务成功，流程流转到下一节点");
                }
            } else {
                log.warn("【资产变动-提交】当前任务不是submit节点，跳过自动完成，当前节点：{}",
                        currentTask.getTaskDefinitionKey());
            }
        } else {
            log.warn("【资产变动-提交】获取当前任务失败，流程实例：{}", procInstId);
        }

        // 保存流程信息
        Change updateChange = new Change();
        updateChange.setId(changeId);
        updateChange.setProcInstId(procInstId);
        updateChange.setBusinessStatus(BusinessStatusConstants.PENDING);
        log.info("【资产变动-提交】步骤4：保存流程信息，变动单ID：{}，流程实例ID：{}，业务状态：{}",
                changeId, procInstId, BusinessStatusConstants.PENDING);
        changeMapper.updateById(updateChange);

        // 缓存拟变更资产数据到 Redis
        if (!change.getAssets().isEmpty()) {
            try {
                String redisKey = RedisConstants.ASSET_CHANGE_DRAFT_PREFIX + changeId;
                String json = JSONUtil.toJsonStr(change.getAssets());
                stringRedisTemplate.opsForValue().set(redisKey, json,
                        RedisConstants.ASSET_CHANGE_DRAFT_TTL, TimeUnit.DAYS);
                log.info("【资产变动-提交】步骤5：保存拟变更资产数据到Redis，变动单ID：{}，资产数量：{}，缓存key：{}",
                        changeId, change.getAssets().size(), redisKey);
            } catch (Exception e) {
                log.error("【资产变动-提交】保存拟变更资产数据到Redis失败，变动单ID：{}", changeId, e);
            }
        }

        log.info("【资产变动-提交】全部完成，变动单ID：{}，流程实例ID：{}", changeId, procInstId);
        return changeId;
    }

    /**
     * 审批资产变动单
     * <p>
     * 执行审批操作，完成工作流任务。
     * <ul>
     *   <li><b>审批通过：</b>异步执行资产变更（批量更新资产信息），状态变更为已完成</li>
     *   <li><b>审批驳回：</b>不执行资产变更，流程回到submit节点等待重新提交</li>
     * </ul>
     * </p>
     *
     * @param id         变动单ID
     * @param approved   是否通过（true-通过，false-驳回）
     * @param comment    审批意见
     * @param approverId 审批人ID
     * @return 影响行数，成功返回1
     * @throws ServiceException 单据不存在、未提交、流程已结束、无权审批时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int approveChange(Long id, boolean approved, String comment, Long approverId) {
        log.info("【资产变动-审批】开始，变动单ID：{}，审批结果：{}，审批人：{}，审批意见：{}",
                id, approved ? "通过" : "驳回", approverId, comment);

        Change change = changeMapper.selectById(id);
        if (change == null) {
            throw new ServiceException("单据不存在");
        }
        if (StringUtils.isEmpty(change.getProcInstId())) {
            throw new ServiceException("未提交申请单据");
        }
        if (SecurityUtils.getUserId().equals(change.getApplicantId())) {
            throw new ServiceException("不能审批本人提交的申请");
        }

        // 获取当前任务
        R<CurrentTaskVO> taskResult = remoteWorkflowService.getCurrentTask(change.getProcInstId());
        if (taskResult.getCode() != R.SUCCESS || taskResult.getData() == null) {
            if (isProcessEnded(change.getProcInstId())) {
                throw new ServiceException("流程已结束,无法进行审批");
            }
            throw new ServiceException("未找到待办任务");
        }

        CurrentTaskVO currentTask = taskResult.getData();
        log.info("【资产变动-审批】当前任务信息：taskId={}, taskName={}, taskDefinitionKey={}, assignee={}",
                currentTask.getTaskId(), currentTask.getTaskName(),
                currentTask.getTaskDefinitionKey(), currentTask.getAssignee());

        log.info("【资产变动-审批】开始完成工作流任务，taskId：{}", currentTask.getTaskId());
        CompleteTask completeDTO = new CompleteTask();
        completeDTO.setTaskId(currentTask.getTaskId());
        completeDTO.setApproved(approved);
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

        if (approved) {
            log.info("【资产变动-审批】审批通过，执行资产变更，变动单ID：{}", id);
            executeChange(id);
            log.info("【资产变动-审批】资产变更执行成功");
        } else {
            log.info("【资产变动-审批】审批驳回，不执行资产变更，流程回到 submit 节点");
        }

        updateBusinessStatusAfterApproval(change);
        log.info("【资产变动-审批】审批完成，变动单ID：{}，审批结果：{}", id, approved ? "通过" : "驳回");
        return 1;
    }

    /**
     * 撤回变动单
     * <p>
     * 仅允许申请人撤回流程中的变动单。
     * 撤回后删除流程实例，业务状态恢复为草稿(DRAFT)。
     * </p>
     *
     * @param id 变动单ID
     * @return 影响行数，成功返回1
     * @throws ServiceException 单据不存在、流程未启动、流程已结束、无权操作时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int withdrawChange(Long id) {
        log.info("【资产变动-撤回】开始，变动单ID：{}", id);

        Change change = changeMapper.selectById(id);
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
        changeMapper.updateById(updateChange);

        log.info("【资产变动-撤回】撤回成功，变动单ID：{}，业务状态设置为DRAFT", id);
        return 1;
    }

    /**
     * 导出PDF
     * <p>
     * 【优化点】使用 IO_EXECUTOR 线程池异步查询变动单数据，
     * 提升接口响应速度。PDF生成本身是IO密集型操作，必须同步执行。
     * </p>
     *
     * @param response HTTP响应对象，用于输出PDF流
     * @param id       变动单ID
     * @throws Exception PDF生成或导出过程中的异常
     */
    @Override
    public void exportPdf(HttpServletResponse response, Long id) throws Exception {
        // 使用 IO_EXECUTOR 线程池异步查询变动单数据
        CompletableFuture<ChangeVO> future = CompletableFuture.supplyAsync(
                () -> selectChangeById(id),
                IO_EXECUTOR
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

    /**
     * 执行资产变更（在调用方事务内同步批量更新）
     * <p>
     * 【修复说明】原实现使用 {@link CompletableFuture} 结合业务线程池并行更新，
     * 导致数据库写操作脱离当前事务，且自调用使 {@code @Transactional} 失效，
     * 审批通过后资产变更失败无法回滚。现改为在当前事务内顺序批量更新，
     * 保证"审批 + 资产变更"的本地事务一致性。
     * </p>
     * <p>
     * <b>执行流程：</b>
     * <ol>
     *   <li>从Redis读取拟变更数据（带降级兜底）</li>
     *   <li>设置公共字段（更新人）</li>
     *   <li>使用Guava Lists.partition分段，每批100条</li>
     *   <li>顺序批量更新，每批带Guava Retry重试，任一批失败抛出异常整体回滚</li>
     *   <li>清理Redis缓存（带重试）</li>
     * </ol>
     * </p>
     *
     * @param changeId 变动单ID
     * @throws ServiceException 资产变更失败时抛出
     */
    void executeChange(Long changeId) {
        log.info("【资产变动-执行】开始执行资产变更，变动单ID：{}", changeId);

        // 1. 从 Redis 读取拟变更数据（带降级）
        List<Assets> pendingAssets = getPendingAssetsFromRedis(changeId);

        if (pendingAssets == null || pendingAssets.isEmpty()) {
            log.warn("【资产变动-执行】没有资产需要变更");
            return;
        }

        // 设置公共字段
        pendingAssets.forEach(asset -> asset.setUpdateBy(SecurityUtils.getUsername()));

        // 2. 使用 Guava Lists.partition 分段，每批 100 条
        List<List<Assets>> partitions = Lists.partition(pendingAssets, 100);
        log.info("【资产变动-执行】资产总数：{}，分段数：{}，每批大小：100",
                pendingAssets.size(), partitions.size());

        // 3. 顺序批量更新：在同一事务内执行，保证审批与资产变更的原子性，任一批失败即整体回滚
        for (List<Assets> batch : partitions) {
            boolean result = RetryUtils.executeWithDbRetry(
                    () -> {
                        boolean success = assetsService.updateBatchById(batch);
                        if (!success) {
                            throw new RuntimeException("数据库操作返回false");
                        }
                        return success;
                    },
                    String.format("批量更新资产失败，批次大小：%d", batch.size())
            );
            if (!result) {
                throw new ServiceException(String.format("资产变更失败，批次大小：%d", batch.size()));
            }
        }

        log.info("【资产变动-执行】资产变更执行完成，变动单ID：{}，资产总数：{}", changeId, pendingAssets.size());

        // 4. 清理Redis缓存（带重试）
        String redisKey = RedisConstants.ASSET_CHANGE_DRAFT_PREFIX + changeId;
        RetryUtils.executeWithRedisRetry(
                () -> stringRedisTemplate.delete(redisKey),
                String.format("清理Redis缓存失败，key：%s", redisKey)
        );
        log.info("【资产变动-执行】清理 Redis 缓存成功，key：{}", redisKey);
    }

    /**
     * 从 Redis 读取拟变更数据（带降级）
     * <p>
     * 【优化点】Redis 读取失败或数据不存在时，降级到数据库直查。
     * </p>
     *
     * @param changeId 变动单ID
     * @return 拟变更资产列表
     */
    private List<Assets> getPendingAssetsFromRedis(Long changeId) {
        String redisKey = RedisConstants.ASSET_CHANGE_DRAFT_PREFIX + changeId;

        try {
            // 主链路：读 Redis
            String assetsJson = stringRedisTemplate.opsForValue().get(redisKey);

            if (StringUtils.isNotEmpty(assetsJson)) {
                List<Assets> assets = JSONUtil.toList(JSONUtil.parseArray(assetsJson), Assets.class);
                log.info("【资产变动-Redis】从 Redis 读取拟变更数据成功，变动单ID：{}，资产数量：{}",
                        changeId, assets.size());
                return assets;
            }

            // Redis 中没有数据，降级到数据库
            log.warn("【资产变动-Redis降级】Redis 中无数据，降级到数据库查询，变动单ID：{}", changeId);
            return changeMapper.selectAssetsByChangeId(changeId);

        } catch (Exception e) {
            // Redis 异常，降级到数据库
            log.error("【资产变动-Redis降级】Redis 读取异常，降级到数据库查询，变动单ID：{}，异常：{}",
                    changeId, e.getMessage());
            return changeMapper.selectAssetsByChangeId(changeId);
        }
    }

    /**
     * 异步并行保存附件（IO密集型）
     * <p>
     * 【优化点】使用 IO_EXECUTOR 线程池并行保存每个附件。
     * </p>
     *
     * @param changeId    变动单ID
     * @param attachments 附件列表
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

        // 使用 IO_EXECUTOR 线程池并行保存附件
        List<CompletableFuture<Boolean>> futures = attachments.stream()
                .map(attachment -> CompletableFuture.supplyAsync(
                        () -> changeAttachmentService.save(attachment),
                        IO_EXECUTOR
                ))
                .collect(Collectors.toList());

        // 等待所有保存完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        long successCount = futures.stream().filter(CompletableFuture::join).count();
        log.info("【资产变动-附件】并行保存完成，变动单ID：{}，成功：{}，总数：{}",
                changeId, successCount, attachments.size());
    }

    /**
     * 初始化变动单基本信息
     *
     * @param change 变动视图对象
     */
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

    /**
     * 更新变动单业务数据
     *
     * @param existing 已存在的变动单
     * @param change   新的变动数据
     * @param changeId 变动单ID
     */
    private void updateChangeBusinessData(Change existing, ChangeVO change, Long changeId) {
        log.info("【资产变动-业务数据更新】开始，变动单ID：{}", changeId);

        existing.setChangeType(change.getChangeType());
        existing.setChangeReason(change.getChangeReason());
        existing.setRemark(change.getRemark());
        existing.setUpdateBy(SecurityUtils.getUsername());
        changeMapper.updateById(existing);
        log.info("【资产变动-业务数据更新】更新主表成功");

        changeMapper.deleteDetailByMasterId(changeId);
        saveAssetRelations(changeId, change.getAssets());

        deleteAttachmentsByChangeId(changeId);
        saveAttachmentsAsync(changeId, change.getAttachments());

        log.info("【资产变动-业务数据更新】完成");
    }

    /**
     * 保存资产关联关系
     *
     * @param changeId 变动单ID
     * @param assets   资产列表
     */
    private void saveAssetRelations(Long changeId, List<Assets> assets) {
        if (assets.isEmpty()) {
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

    /**
     * 获取变动单附件列表
     *
     * @param changeId 变动单ID
     * @return 附件列表
     */
    private List<ChangeAttachment> getAttachmentsByChangeId(Long changeId) {
        ChangeAttachment query = new ChangeAttachment();
        query.setMasterId(changeId);
        return changeAttachmentService.selectChangeAttachmentList(query);
    }

    /**
     * 删除变动单附件
     *
     * @param changeId 变动单ID
     */
    private void deleteAttachmentsByChangeId(Long changeId) {
        changeAttachmentService.deleteByMasterId(changeId);
    }

    /**
     * 判断流程是否已结束
     *
     * @param procInstId 流程实例ID
     * @return true-已结束，false-未结束或查询失败
     */
    private boolean isProcessEnded(String procInstId) {
        try {
            R<Boolean> result = remoteWorkflowService.isProcessEnded(procInstId);
            return result != null && result.getCode() == R.SUCCESS && Boolean.TRUE.equals(result.getData());
        } catch (Exception e) {
            log.error("【资产变动-检查流程】检查流程是否结束失败，procInstId={}", procInstId, e);
            return false;
        }
    }

    /**
     * 审批后更新业务状态
     *
     * @param change 变动单对象
     */
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
            if (endedResult != null && endedResult.getCode() == R.SUCCESS
                    && Boolean.TRUE.equals(endedResult.getData())) {
                log.info("【资产变动-状态更新】流程已结束，设置业务状态为 COMPLETED");
                updateChange.setBusinessStatus(BusinessStatusConstants.COMPLETED);
                changeMapper.updateById(updateChange);
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
                } else {
                    log.info("【资产变动-状态更新】流程未结束，当前节点：{}，业务状态保持 PENDING", currentTaskKey);
                    updateChange.setBusinessStatus(BusinessStatusConstants.PENDING);
                }
                changeMapper.updateById(updateChange);
                log.info("【资产变动-状态更新】业务状态更新成功");
                return;
            }

            log.warn("【资产变动-状态更新】无法获取任务状态，暂不更新业务状态");
        } catch (Exception e) {
            log.error("【资产变动-状态更新】更新业务状态失败，变动单ID：{}", change.getId(), e);
        }
    }
}