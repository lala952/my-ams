package com.ruoyi.workflow.service.impl;

import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.system.api.RemoteUserService;
import com.ruoyi.system.api.domain.SysUser;
import com.ruoyi.workflow.api.constant.NodeStatusConstants;
import com.ruoyi.workflow.api.domain.CompleteTask;
import com.ruoyi.workflow.api.domain.StartProcess;
import com.ruoyi.workflow.api.domain.vo.CurrentTaskVO;
import com.ruoyi.workflow.api.domain.vo.ProcessStartVO;
import com.ruoyi.workflow.api.domain.vo.ProcessStatusVO;
import com.ruoyi.workflow.api.domain.vo.TodoTaskVO;
import com.ruoyi.workflow.service.WorkflowService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.workflow.api.domain.vo.ApprovalHistoryVO;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 工作流服务实现类
 */
@Service
public class WorkflowServiceImpl implements WorkflowService {

    private static final Logger log = LoggerFactory.getLogger(WorkflowServiceImpl.class);

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RemoteUserService remoteUserService;

    @Override
    public R<ProcessStartVO> startProcess(StartProcess process, Long userId) {
        try {
            Map<String, Object> variables = process.getVariables();
            if (variables == null) variables = new HashMap<>();
            variables.put("initiator", userId);

            ProcessInstance instance = runtimeService.startProcessInstanceByKey(
                    process.getProcessKey(), process.getBusinessKey(), variables
            );
            log.debug("【工作流-启动流程实例】instance.getStartUserId:{}", instance.getStartUserId());
            log.debug("流程实例发起人：{}",instance.getStartUserId());
            ProcessStartVO result = new ProcessStartVO(instance.getId(), instance.getProcessDefinitionId(), process.getBusinessKey());
            return R.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail("启动流程失败: " + e.getMessage());
        }
    }

    @Override
    public R<Void> completeTask(CompleteTask c, Long userId) {
        try {
            Task task = taskService.createTaskQuery().taskId(c.getTaskId()).singleResult();
            if (task == null) {
                log.error("【工作流-完成任务】任务不存在或已被处理，taskId={}", c.getTaskId());
                return R.fail("任务不存在或已被处理");
            }

            log.info("【工作流-完成任务】开始处理，taskId={}, taskName={}, userId={}, approved={}",
                    c.getTaskId(), task.getName(), userId, c.getApproved());

            if (task.getAssignee() == null || task.getAssignee().isEmpty()) {
                log.info("【工作流-自动签收】检测到候选组任务 (taskId={})，自动分配给用户：{}", c.getTaskId(), userId);
                taskService.claim(c.getTaskId(), String.valueOf(userId));
                task = taskService.createTaskQuery().taskId(c.getTaskId()).singleResult();
                log.info("【工作流-自动签收】签收成功，taskId={}, assignee={}", c.getTaskId(), task.getAssignee());
            }

            if (c.getComment() != null) {
                taskService.addComment(c.getTaskId(), task.getProcessInstanceId(), c.getComment());
                log.debug("【工作流-完成任务】添加审批意见：{}", c.getComment());
            }

            Map<String, Object> variables = c.getVariables();
            if (variables == null) variables = new HashMap<>();

            if (!variables.isEmpty()) {
                taskService.setVariables(c.getTaskId(), variables);
            }

            taskService.setVariableLocal(c.getTaskId(), "approved", c.getApproved());
            taskService.complete(c.getTaskId());

            log.info("【工作流-完成任务】完成成功，taskId={}, taskDefinitionKey={}, approved={}, assignee={}",
                    c.getTaskId(), task.getTaskDefinitionKey(), c.getApproved(), task.getAssignee());

            return R.ok();
        } catch (Exception e) {
            log.error("【工作流-完成任务】失败，taskId={}", c.getTaskId(), e);
            return R.fail("办理任务失败: " + e.getMessage());
        }
    }

    @Override
    public R<CurrentTaskVO> getCurrentTask(String procInstId) {
        try {
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(procInstId).list();
            if (tasks.isEmpty()) {
                log.warn("【工作流-当前任务】流程实例 {} 无待办任务", procInstId);
                return R.fail("当前无待办任务");
            }

            Task task = tasks.get(0);
            CurrentTaskVO result = new CurrentTaskVO();

            String assignee = task.getAssignee();
            if (StringUtils.isNotEmpty(assignee)) {
                try {
                    Long assigneeId = Long.parseLong(assignee);
                    SysUser user = getUserById(assigneeId);
                    result.setAssignee(assignee);
                    result.setAssigneeName(user != null ? user.getNickName() : "用户" + assigneeId);
                    log.debug("【工作流-当前任务】任务已签收，办理人：{}", result.getAssigneeName());
                } catch (Exception e) {
                    log.warn("【工作流-当前任务】获取办理人信息失败，assignee={}", assignee, e);
                    result.setAssignee(assignee);
                    result.setAssigneeName("用户" + assignee);
                }
            } else {
                result.setAssignee(null);
                result.setAssigneeName(null);
                log.debug("【工作流-当前任务】候选组任务，待签收，taskId={}", task.getId());
            }

            result.setTaskId(task.getId());
            result.setTaskName(task.getName());
            result.setTaskDefinitionKey(task.getTaskDefinitionKey());
            result.setCreateTime(task.getCreateTime());
            result.setProcInstId(task.getProcessInstanceId());
            result.setProcDefId(task.getProcessDefinitionId());

            log.info("【工作流-当前任务】获取成功，taskId={}, taskName={}, assignee={}",
                    task.getId(), task.getName(), result.getAssigneeName());
            return R.ok(result);
        } catch (Exception e) {
            log.error("【工作流-当前任务】获取失败，procInstId={}", procInstId, e);
            return R.fail("获取任务失败: " + e.getMessage());
        }
    }

    @Override
    public R<List<ApprovalHistoryVO>> getApprovalHistory(String procInstId) {
        try {
            HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(procInstId)
                    .singleResult();

            if (hpi == null) {
                log.warn("【工作流-审批历史】流程实例不存在，procInstId={}", procInstId);
                return R.fail("流程实例不存在");
            }

            List<Task> activeTasks = taskService.createTaskQuery()
                    .processInstanceId(procInstId)
                    .list();
            Set<String> activeTaskKeys = activeTasks.stream()
                    .map(Task::getTaskDefinitionKey)
                    .collect(Collectors.toSet());

            List<HistoricTaskInstance> tasks = historyService.createHistoricTaskInstanceQuery()
                    .processInstanceId(procInstId)
                    .orderByHistoricTaskInstanceEndTime().asc()
                    .list();

            List<ApprovalHistoryVO> historyList = new ArrayList<>();

            for (HistoricTaskInstance task : tasks) {
                ApprovalHistoryVO item = new ApprovalHistoryVO();

                String assignee = task.getAssignee();
                if (StringUtils.isNotEmpty(assignee)) {
                    try {
                        Long assigneeId = Long.parseLong(assignee);
                        SysUser user = getUserById(assigneeId);
                        item.setAssignee(assignee);
                        item.setAssigneeName(user != null ? user.getNickName() : "用户" + assigneeId);
                    } catch (Exception e) {
                        log.warn("【工作流-审批历史】获取审批人信息失败，assignee={}", assignee, e);
                        item.setAssignee(assignee);
                        item.setAssigneeName("用户" + assignee);
                    }
                } else {
                    item.setAssignee(null);
                    item.setAssigneeName("待签收");
                }

                item.setTaskId(task.getId());
                item.setTaskName(task.getName());
                item.setTaskDefinitionKey(task.getTaskDefinitionKey());
                item.setStartTime(task.getStartTime());
                item.setEndTime(task.getEndTime());
                item.setDurationInMillis(task.getDurationInMillis());

                if (task.getEndTime() != null) {
                    item.setNodeStatus(NodeStatusConstants.COMPLETED);
                } else if (activeTaskKeys.contains(task.getTaskDefinitionKey())) {
                    item.setNodeStatus(NodeStatusConstants.ACTIVE);
                } else {
                    item.setNodeStatus(NodeStatusConstants.PENDING);
                }

                // 根据历史任务ID获取审批意见，确保每个任务只显示自己的意见
                List<Comment> comments = taskService.getTaskComments(task.getId());
                if (comments != null && !comments.isEmpty()) {
                    item.setComment(comments.get(0).getFullMessage());
                    log.debug("【工作流-审批历史】taskId={}, 审批意见={}", task.getId(), comments.get(0).getFullMessage());
                } else {
                    item.setComment(null);
                }

                Boolean approved = null;
                try {
                    List<HistoricVariableInstance> vars = historyService.createHistoricVariableInstanceQuery()
                            .processInstanceId(task.getProcessInstanceId())
                            .taskId(task.getId())
                            .variableName("approved")
                            .list();

                    if (vars != null && !vars.isEmpty()) {
                        approved = (Boolean) vars.get(0).getValue();
                        log.debug("【工作流-审批历史】taskId={}, taskDefinitionKey={}, approved={}",
                                task.getId(), task.getTaskDefinitionKey(), approved);
                    }
                } catch (Exception e) {
                    log.warn("【工作流-审批历史】获取任务 {} 的审批结果失败", task.getId(), e);
                }

                item.setApproved(approved);
                historyList.add(item);
            }

            log.info("【工作流-审批历史】流程实例 {}，获取到 {} 条历史记录", procInstId, historyList.size());
            return R.ok(historyList);
        } catch (Exception e) {
            log.error("【工作流-审批历史】获取失败，procInstId={}", procInstId, e);
            return R.fail("获取历史失败: " + e.getMessage());
        }
    }

    @Override
    public R<Boolean> isProcessEnded(String procInstId) {
        try {
            ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(procInstId).singleResult();
            if (pi != null) return R.ok(false);

            HistoricProcessInstance hi = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(procInstId).singleResult();
            return R.ok(hi != null && hi.getEndTime() != null);
        } catch (Exception e) {
            return R.fail("检查状态失败: " + e.getMessage());
        }
    }

    @Override
    public R<String> getProcessXml(String procDefId) {
        try {
            org.activiti.engine.repository.ProcessDefinition pd = repositoryService
                    .createProcessDefinitionQuery().processDefinitionId(procDefId).singleResult();
            if (pd == null) return R.fail("流程定义不存在");

            InputStream is = repositoryService.getResourceAsStream(pd.getDeploymentId(), pd.getResourceName());
            String xml = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));
            is.close();
            return R.ok(xml);
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail("获取 XML 失败: " + e.getMessage());
        }
    }

    @Override
    public R<ProcessStatusVO> getProcessStatus(String procInstId) {
        try {
            ProcessStatusVO result = new ProcessStatusVO();
            result.setProcInstId(procInstId);

            HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(procInstId).singleResult();
            if (hpi != null) {
                result.setProcDefId(hpi.getProcessDefinitionId());
                result.setProcessEnded(hpi.getEndTime() != null);
                result.setEndTime(hpi.getEndTime());
                result.setStartTime(hpi.getStartTime());
                result.setBusinessKey(hpi.getBusinessKey());

                List<HistoricVariableInstance> vars = historyService.createHistoricVariableInstanceQuery()
                        .processInstanceId(procInstId)
                        .variableName("initiator")
                        .list();
                if (vars != null && !vars.isEmpty()) {
                    result.setInitiator((Long) vars.get(0).getValue());
                }
            }

            List<String> activeIds = runtimeService.getActiveActivityIds(procInstId);
            result.setActiveActivityIds(activeIds != null ? activeIds : new ArrayList<>());

            List<HistoricTaskInstance> finishedTasks = historyService.createHistoricTaskInstanceQuery()
                    .processInstanceId(procInstId).finished().list();
            List<String> finishedKeys = finishedTasks.stream()
                    .map(HistoricTaskInstance::getTaskDefinitionKey)
                    .collect(Collectors.toList());
            result.setFinishedActivityIds(finishedKeys);

            return R.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail("获取状态失败: " + e.getMessage());
        }
    }

    @Override
    public R<Void> deleteProcessInstance(String procInstId, String reason) {
        try {
            runtimeService.deleteProcessInstance(procInstId, reason != null ? reason : "用户主动终止");
            return R.ok();
        } catch (Exception e) {
            return R.fail("删除实例失败: " + e.getMessage());
        }
    }

    @Override
    public R<List<TodoTaskVO>> getTodoTaskList(Long userId) {
        try {
            List<Task> tasks = taskService.createTaskQuery()
                    .taskCandidateOrAssigned(String.valueOf(userId))
                    .orderByTaskCreateTime().desc()
                    .list();

            List<TodoTaskVO> todoList = new ArrayList<>();
            for (Task task : tasks) {
                TodoTaskVO vo = new TodoTaskVO();
                vo.setTaskId(task.getId());
                vo.setTaskName(task.getName());
                vo.setTaskDefinitionKey(task.getTaskDefinitionKey());
                vo.setAssignee(task.getAssignee());
                vo.setCreateTime(task.getCreateTime());
                vo.setProcInstId(task.getProcessInstanceId());
                vo.setProcDefId(task.getProcessDefinitionId());
                vo.setPriority(task.getPriority());
                vo.setDescription(task.getDescription());

                try {
                    org.activiti.engine.repository.ProcessDefinition pd = repositoryService
                            .createProcessDefinitionQuery()
                            .processDefinitionId(task.getProcessDefinitionId())
                            .singleResult();
                    if (pd != null) {
                        vo.setProcessDefinitionKey(pd.getKey());
                        vo.setProcessName(pd.getName());
                    }
                } catch (Exception e) {
                    log.warn("获取流程定义信息失败: {}", task.getProcessDefinitionId(), e);
                }

                try {
                    ProcessInstance pi = runtimeService
                            .createProcessInstanceQuery()
                            .processInstanceId(task.getProcessInstanceId())
                            .singleResult();
                    if (pi != null) {
                        vo.setBusinessKey(pi.getBusinessKey());
                    }
                } catch (Exception e) {
                    log.warn("获取业务 Key 失败: {}", task.getProcessInstanceId(), e);
                }

                String assignee = task.getAssignee();
                if (StringUtils.isNotEmpty(assignee)) {
                    try {
                        Long assigneeId = Long.parseLong(assignee);
                        getUserResultForTodo(vo, assigneeId);
                    } catch (Exception e) {
                        log.warn("查询办理人信息失败: {}", task.getAssignee(), e);
                        vo.setAssigneeName("用户" + assignee);
                    }
                } else {
                    vo.setAssigneeName("待签收");
                }

                todoList.add(vo);
            }

            log.info("【工作流-查询待办】用户 {} 的待办任务数量：{}", userId, todoList.size());
            return R.ok(todoList);
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail("查询待办任务失败: " + e.getMessage());
        }
    }

    private void getUserResultForTodo(TodoTaskVO vo, Long assigneeId) {
        R<SysUser> res = remoteUserService.getUserById(assigneeId, SecurityConstants.INNER);
        if (res != null && res.getCode() == 200 && res.getData() != null) {
            vo.setAssigneeName(res.getData().getNickName());
        } else {
            vo.setAssigneeName("用户" + assigneeId);
        }
    }

    public SysUser getUserById(Long userId) {
        if (userId == null) {
            log.warn("【工作流-获取用户】userId 为 null");
            throw new ServiceException("获取用户信息失败: userId为空");
        }
        try {
            R<SysUser> res = remoteUserService.getUserById(userId, SecurityConstants.INNER);
            if (res.getCode() == R.SUCCESS && res.getData() != null) {
                return res.getData();
            } else {
                log.warn("【工作流-获取用户】获取用户信息失败，userId={}, code={}", userId, res.getCode());
                throw new ServiceException("获取用户信息失败: " + (res.getMsg() != null ? res.getMsg() : "用户不存在"));
            }
        } catch (Exception e) {
            log.error("【工作流-获取用户】调用远程服务异常，userId={}", userId, e);
            throw new ServiceException("获取用户信息失败: " + e.getMessage());
        }
    }
}