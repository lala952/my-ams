package com.ruoyi.workflow.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.workflow.api.domain.vo.ApprovalHistoryVO;
import com.ruoyi.workflow.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 负责审批历史记录、流程轨迹等查询功能
 */
@RestController
@RequestMapping("/workflow/history")
public class HistoryController {

    @Autowired
    private WorkflowService workflowService;

    /**
     * 获取审批历史记录
     *
     * @param procInstId 流程实例ID
     * @return 按时间排序的审批历史列表
     */
    @GetMapping("/approval/{procInstId}")
    public R<List<ApprovalHistoryVO>> getApprovalHistory(@PathVariable String procInstId) {
        return workflowService.getApprovalHistory(procInstId);
    }
}
