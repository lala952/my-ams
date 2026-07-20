package com.ruoyi.workflow.service;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.workflow.api.domain.CompleteTask;
import com.ruoyi.workflow.api.domain.StartProcess;
import com.ruoyi.workflow.api.domain.vo.ApprovalHistoryVO;
import com.ruoyi.workflow.api.domain.vo.CurrentTaskVO;
import com.ruoyi.workflow.api.domain.vo.ProcessStartVO;
import com.ruoyi.workflow.api.domain.vo.ProcessStatusVO;
import com.ruoyi.workflow.api.domain.vo.TodoTaskVO;

import java.util.List;

/**
 * 工作流服务接口
 * <p>提供流程启动、任务办理、历史查询等核心工作流功能</p>
 */
public interface WorkflowService {

    /**
     * 启动工作流程
     * <p>业务流程：</p>
     * <ol>
     *   <li>验证流程定义是否存在</li>
     *   <li>设置流程发起人 (initiator)</li>
     *   <li>启动流程实例并关联业务键</li>
     *   <li>返回流程实例信息</li>
     * </ol>
     *
     * @param dto    启动参数（包含流程Key、业务Key、流程变量）
     * @param userId 发起人ID
     * @return 流程实例信息（流程实例ID、流程定义ID等）
     */
    R<ProcessStartVO> startProcess(StartProcess dto, Long userId);

    /**
     * 完成工作任务
     * <p>业务流程：</p>
     * <ol>
     *   <li>验证任务是否存在且未被处理</li>
     *   <li>添加审批意见</li>
     *   <li>设置审批结果变量</li>
     *   <li>完成任务，流转到下一节点</li>
     * </ol>
     *
     * @param dto    任务参数（包含任务ID、审批结果、审批意见）
     * @param userId 办理人ID
     * @return 操作结果
     */
    R<Void> completeTask(CompleteTask dto, Long userId);

    /**
     * 获取当前待办任务
     *
     * @param procInstId 流程实例ID
     * @return 当前任务信息（如有多个任务，返回第一个）
     */
    R<CurrentTaskVO> getCurrentTask(String procInstId);

    /**
     * 获取审批历史记录
     *
     * @param procInstId 流程实例ID
     * @return 按时间排序的审批历史列表
     */
    R<List<ApprovalHistoryVO>> getApprovalHistory(String procInstId);

    /**
     * 检查流程是否已结束
     *
     * @param procInstId 流程实例ID
     * @return true-已结束，false-运行中
     */
    R<Boolean> isProcessEnded(String procInstId);

    /**
     * 获取流程定义XML
     *
     * @param procDefId 流程定义ID
     * @return BPMN XML内容
     */
    R<String> getProcessXml(String procDefId);

    /**
     * 获取流程状态详情
     * <p>包含：</p>
     * <ul>
     *   <li>流程基本信息（定义ID、业务Key等）</li>
     *   <li>当前活跃节点</li>
     *   <li>已完成节点</li>
     * </ul>
     *
     * @param procInstId 流程实例ID
     * @return 流程状态详细信息
     */
    R<ProcessStatusVO> getProcessStatus(String procInstId);

    /**
     * 删除流程实例
     *
     * @param procInstId 流程实例ID
     * @param reason     删除原因（可选）
     * @return 操作结果
     */
    R<Void> deleteProcessInstance(String procInstId, String reason);

    /**
     * 查询个人待办任务列表
     * <p>根据当前登录用户ID查询其所有待办任务</p>
     *
     * @param userId 用户ID
     * @return 待办任务列表
     */
    R<List<TodoTaskVO>> getTodoTaskList(Long userId);
}
