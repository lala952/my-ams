package com.ruoyi.workflow.api;

import com.ruoyi.common.core.constant.ServiceNameConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.workflow.api.domain.CompleteTask;
import com.ruoyi.workflow.api.domain.StartProcess;
import com.ruoyi.workflow.api.domain.vo.ApprovalHistoryVO;
import com.ruoyi.workflow.api.domain.vo.CurrentTaskVO;
import com.ruoyi.workflow.api.domain.vo.ProcessStartVO;
import com.ruoyi.workflow.api.domain.vo.ProcessStatusVO;
import com.ruoyi.workflow.api.factory.RemoteWorkflowFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 工作流远程服务接口（Feign）
 *
 * <p>供其他微服务调用工作流功能</p>
 */
@FeignClient(contextId = "remoteWorkflowService",
        value = ServiceNameConstants.WORKFLOW_SERVICE,
        fallbackFactory = RemoteWorkflowFallbackFactory.class)
public interface RemoteWorkflowService {

    /**
     * 启动工作流程
     *
     * @param dto 启动参数
     * @return 流程实例信息
     */
    @PostMapping("/workflow/process/start")
    R<ProcessStartVO> startProcess(@RequestBody StartProcess dto);

    /**
     * 完成工作任务
     *
     * @param dto 任务参数
     * @return 操作结果
     */
    @PostMapping("/workflow/task/complete")
    R<Void> completeTask(@RequestBody CompleteTask dto);

    /**
     * 获取当前待办任务
     *
     * @param procInstId 流程实例ID
     * @return 当前任务信息
     */
    @GetMapping("/workflow/task/current/{procInstId}")
    R<CurrentTaskVO> getCurrentTask(@PathVariable("procInstId") String procInstId);

    /**
     * 获取审批历史记录
     *
     * @param procInstId 流程实例ID
     * @return 审批历史列表
     */
    @GetMapping("/workflow/history/approval/{procInstId}")
    R<List<ApprovalHistoryVO>> getApprovalHistory(@PathVariable("procInstId") String procInstId);

    /**
     * 检查流程是否已结束
     *
     * @param procInstId 流程实例ID
     * @return true-已结束，false-运行中
     */
    @GetMapping("/workflow/process/isEnded/{procInstId}")
    R<Boolean> isProcessEnded(@PathVariable("procInstId") String procInstId);

    /**
     * 获取流程定义XML
     *
     * @param procDefId 流程定义ID
     * @return BPMN XML内容
     */
    @GetMapping("/workflow/process/xml/{procDefId}")
    R<String> getProcessXml(@PathVariable("procDefId") String procDefId);

    /**
     * 获取流程状态详情
     *
     * @param procInstId 流程实例ID
     * @return 流程状态信息
     */
    @GetMapping("/workflow/process/status/{procInstId}")
    R<ProcessStatusVO> getProcessStatus(@PathVariable("procInstId") String procInstId);

    /**
     * 删除/终止流程实例
     *
     * @param procInstId 流程实例ID
     * @param reason     删除原因
     * @return 操作结果
     */
    @DeleteMapping("/workflow/process/{procInstId}")
    R<Void> deleteProcessInstance(@PathVariable("procInstId") String procInstId,
                                  @RequestParam(value = "reason", required = false) String reason);
}
