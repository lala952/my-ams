package com.ruoyi.workflow.api.factory;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.workflow.api.RemoteWorkflowService;
import com.ruoyi.workflow.api.domain.CompleteTask;
import com.ruoyi.workflow.api.domain.StartProcess;
import com.ruoyi.workflow.api.domain.vo.ApprovalHistoryVO;
import com.ruoyi.workflow.api.domain.vo.CurrentTaskVO;
import com.ruoyi.workflow.api.domain.vo.ProcessStartVO;
import com.ruoyi.workflow.api.domain.vo.ProcessStatusVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.List;

/**
 * 工作流远程服务降级工厂
 * 当工作流服务调用失败时提供降级处理，返回对应的失败结果
 *
 * @author ruoyi
 * @date 2026-09-03
 */
public class RemoteWorkflowFallbackFactory implements FallbackFactory<RemoteWorkflowService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteWorkflowFallbackFactory.class);

    /**
     * 创建工作流服务降级实例
     *
     * @param throwable 调用失败原因
     * @return 降级后的工作流服务实现
     */
    @Override
    public RemoteWorkflowService create(Throwable throwable) {
        log.error("工作流服务调用失败:{}",throwable.getMessage());
        return new RemoteWorkflowService() {
            /**
             * 降级：启动流程
             *
             * @param dto 启动参数
             * @return 失败结果
             */
            @Override
            public R<ProcessStartVO> startProcess(StartProcess dto) {
                return R.fail("启动流程失败：" + throwable.getMessage());
            }

            /**
             * 降级：办理任务
             *
             * @param dto 任务参数
             * @return 失败结果
             */
            @Override
            public R<Void> completeTask(CompleteTask dto) {
                return R.fail("办理任务失败：" + throwable.getMessage());
            }

            /**
             * 降级：获取当前任务
             *
             * @param procInstId 流程实例ID
             * @return 失败结果
             */
            @Override
            public R<CurrentTaskVO> getCurrentTask(String procInstId) {
                return R.fail("获取当前任务失败：" + throwable.getMessage());
            }

            /**
             * 降级：获取审批历史
             *
             * @param procInstId 流程实例ID
             * @return 失败结果
             */
            @Override
            public R<List<ApprovalHistoryVO>> getApprovalHistory(String procInstId) {
                return R.fail("获取审批历史失败：" + throwable.getMessage());
            }

            /**
             * 降级：判断流程是否结束
             *
             * @param procInstId 流程实例ID
             * @return 失败结果
             */
            @Override
            public R<Boolean> isProcessEnded(String procInstId) {
                return R.fail("判断流程状态失败：" + throwable.getMessage());
            }

            /**
             * 降级：获取流程定义XML
             *
             * @param procDefId 流程定义ID
             * @return 失败结果
             */
            @Override
            public R<String> getProcessXml(String procDefId) {
                return R.fail("获取流程图失败：" + throwable.getMessage());
            }

            /**
             * 降级：获取流程状态
             *
             * @param procInstId 流程实例ID
             * @return 失败结果
             */
            @Override
            public R<ProcessStatusVO> getProcessStatus(String procInstId) {
                return R.fail("获取流程状态失败：" + throwable.getMessage());
            }

            /**
             * 降级：删除流程实例
             *
             * @param procInstId 流程实例ID
             * @param reason     删除原因
             * @return 失败结果
             */
            @Override
            public R<Void> deleteProcessInstance(String procInstId, String reason) {
                return R.fail("删除流程实例失败：" + throwable.getMessage());
            }
        };
    }
}
