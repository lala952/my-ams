package com.ruoyi.workflow.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.workflow.api.domain.CompleteTask;
import com.ruoyi.workflow.api.domain.vo.CurrentTaskVO;
import com.ruoyi.workflow.api.domain.vo.TodoTaskVO;
import com.ruoyi.workflow.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 任务管理 Controller
 *
 * <p>负责任务的办理、查询等核心操作</p>
 */
@RestController
@RequestMapping("/workflow/task")
public class TaskController {

    @Autowired
    private WorkflowService workflowService;

    /**
     * 完成工作任务（审批）
     *
     * @param dto 任务参数
     * @return 操作结果
     */
    @PostMapping("/complete")
    public R<Void> completeTask(@RequestBody CompleteTask dto) {
        return workflowService.completeTask(dto, SecurityUtils.getUserId());
    }

    /**
     * 获取当前待办任务
     *
     * @param procInstId 流程实例ID
     * @return 当前任务信息
     */
    @GetMapping("/current/{procInstId}")
    public R<CurrentTaskVO> getCurrentTask(@PathVariable String procInstId) {
        return workflowService.getCurrentTask(procInstId);
    }

    /**
     * 查询个人待办任务列表
     *
     * @return 待办任务列表
     */
    @GetMapping("/todoList")
    public R<List<TodoTaskVO>> getTodoTaskList() {
        return workflowService.getTodoTaskList(SecurityUtils.getUserId());
    }

}
