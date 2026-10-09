package com.yz.mall.job;

import org.apache.skywalking.apm.toolkit.trace.ActiveSpan;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;

/**
 * XXL-JOB 与 SkyWalking 关联工具。
 * <p>
 * Agent 主要为 HTTP 等入口建 Trace；定时任务跑在执行器线程池上，需 {@link Trace} 建本地 Span，
 * 日志经 {@code TraceIdMDCPatternLogbackLayout} 才会带 {@code tid}。
 */
public final class SkyWalkingXxlJobSupport {

    private SkyWalkingXxlJobSupport() {
    }

    /**
     * 在 SkyWalking 本地 Span 中执行任务逻辑。
     *
     * @param jobHandlerName XXL-JOB handler 名（写入 Span tag）
     * @param action         任务体
     * @throws Exception 任务抛出的异常
     */
    @Trace(operationName = "XXL-JOB")
    public static void run(String jobHandlerName, JobAction action) throws Exception {
        ActiveSpan.tag("xxljob.handler", jobHandlerName == null ? "" : jobHandlerName);
        try {
            action.execute();
        } catch (Exception ex) {
            ActiveSpan.error(ex);
            throw ex;
        }
    }

    /**
     * 当前 SkyWalking TraceId；无 Agent / 无上下文时可能为 {@code N/A}。
     *
     * @return traceId
     */
    public static String currentTraceId() {
        return TraceContext.traceId();
    }

    /**
     * XXL-JOB 任务体。
     */
    @FunctionalInterface
    public interface JobAction {
        /**
         * 执行任务。
         *
         * @throws Exception 业务异常
         */
        void execute() throws Exception;
    }
}
