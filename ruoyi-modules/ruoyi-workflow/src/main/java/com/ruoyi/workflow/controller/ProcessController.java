package com.ruoyi.workflow.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.workflow.api.domain.StartProcess;
import com.ruoyi.workflow.api.domain.vo.ProcessStartVO;
import com.ruoyi.workflow.api.domain.vo.ProcessStatusVO;
import com.ruoyi.workflow.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 流程管理 Controller
 *
 * <p>负责流程实例的生命周期管理：启动、查询状态、终止等</p>
 */
@RestController
@RequestMapping("/workflow/process")
public class ProcessController {

    @Autowired
    private WorkflowService workflowService;

    /**
     * 启动工作流程
     *
     * @param dto 启动参数
     * @return 流程实例信息
     */
    @PostMapping("/start")
    public R<ProcessStartVO> startProcess(@RequestBody StartProcess dto) {
        return workflowService.startProcess(dto, SecurityUtils.getUserId());
    }

    /**
     * 获取流程状态详情
     *
     * @param procInstId 流程实例ID
     * @return 流程状态信息
     */
    @GetMapping("/status/{procInstId}")
    public R<ProcessStatusVO> getProcessStatus(@PathVariable String procInstId) {
        return workflowService.getProcessStatus(procInstId);
    }

    /**
     * 检查流程是否已结束
     *
     * @param procInstId 流程实例ID
     * @return true-已结束，false-运行中
     */
    @GetMapping("/isEnded/{procInstId}")
    public R<Boolean> isProcessEnded(@PathVariable String procInstId) {
        return workflowService.isProcessEnded(procInstId);
    }

    /**
     * 获取流程定义XML
     *
     * @param procDefId 流程定义ID
     * @return BPMN XML内容
     */
    @GetMapping("/xml/{procDefId}")
    public R<String> getProcessXml(@PathVariable String procDefId) {
        return workflowService.getProcessXml(procDefId);
    }

    /**
     * 删除/终止流程实例
     *
     * @param procInstId 流程实例ID
     * @param reason     删除原因（可选）
     * @return 操作结果
     */
    @DeleteMapping("/{procInstId}")
    public R<Void> deleteProcessInstance(
            @PathVariable String procInstId,
            @RequestParam(required = false) String reason) {
        return workflowService.deleteProcessInstance(procInstId, reason);
    }
}
