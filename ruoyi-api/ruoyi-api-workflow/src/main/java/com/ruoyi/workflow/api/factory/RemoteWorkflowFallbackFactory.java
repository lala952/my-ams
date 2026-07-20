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
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RemoteWorkflowFallbackFactory implements FallbackFactory<RemoteWorkflowService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteWorkflowFallbackFactory.class);

    @Override
    public RemoteWorkflowService create(Throwable cause) {
        log.error("工作流服务调用失败: {}", cause.getMessage());
        return new RemoteWorkflowService() {
            @Override
            public R<ProcessStartVO> startProcess(StartProcess dto) {
                return R.fail("启动流程失败：" + cause.getMessage());
            }

            @Override
            public R<Void> completeTask(CompleteTask dto) {
                return R.fail("办理任务失败：" + cause.getMessage());
            }

            @Override
            public R<CurrentTaskVO> getCurrentTask(String procInstId) {
                return R.fail("获取当前任务失败：" + cause.getMessage());
            }

            @Override
            public R<List<ApprovalHistoryVO>> getApprovalHistory(String procInstId) {
                return R.fail("获取审批历史失败：" + cause.getMessage());
            }

            @Override
            public R<Boolean> isProcessEnded(String procInstId) {
                return R.fail("判断流程状态失败：" + cause.getMessage());
            }

            @Override
            public R<String> getProcessXml(String procDefId) {
                return R.fail("获取流程图失败：" + cause.getMessage());
            }

            @Override
            public R<ProcessStatusVO> getProcessStatus(String procInstId) {
                return R.fail("获取流程状态失败：" + cause.getMessage());
            }

            @Override
            public R<Void> deleteProcessInstance(String procInstId, String reason) {
                return R.fail("删除流程实例失败：" + cause.getMessage());
            }
        };
    }
}
