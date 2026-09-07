package com.yz.mall.job;

import co.elastic.apm.api.ElasticApm;
import co.elastic.apm.api.Scope;
import co.elastic.apm.api.Transaction;
import org.slf4j.MDC;

/**
 * XXL-JOB 与 Elastic APM 关联工具。
 * <p>
 * Agent 只会为 HTTP 等入口自动建 Transaction；定时任务跑在执行器线程池上，
 * {@code ElasticApm.currentTransaction()} 默认为 Noop（traceId 为空）。
 * 任务内需手动 {@link ElasticApm#startTransaction()} 并 activate，日志 MDC 才会有 trace.id。
 *
 * @author yunze
 * @since 2026-09-07
 */
public final class ElasticApmXxlJobSupport {

    private static final String TRACE_ID = "trace.id";
    private static final String TRANSACTION_ID = "transaction.id";
    private static final String SPAN_ID = "span.id";

    private ElasticApmXxlJobSupport() {
    }

    /**
     * 在独立 APM Transaction 中执行任务逻辑。
     *
     * @param jobHandlerName XXL-JOB handler 名（作为事务名）
     * @param action         任务体
     * @throws Exception 任务抛出的异常（已 capture 到 APM）
     */
    public static void run(String jobHandlerName, JobAction action) throws Exception {
        Transaction transaction = ElasticApm.startTransaction();
        transaction.setName("XXL-JOB " + jobHandlerName);
        transaction.setType("scheduled");
        try (Scope scope = transaction.activate()) {
            putCorrelationMdc(transaction);
            action.execute();
        } catch (Exception ex) {
            transaction.captureException(ex);
            throw ex;
        } finally {
            clearCorrelationMdc();
            transaction.end();
        }
    }

    private static void putCorrelationMdc(Transaction transaction) {
        String traceId = transaction.getTraceId();
        String txId = transaction.getId();
        if (!traceId.isEmpty()) {
            MDC.put(TRACE_ID, traceId);
        }
        if (!txId.isEmpty()) {
            MDC.put(TRANSACTION_ID, txId);
            // 无子 Span 时用 transaction.id 作为 span.id，便于日志关联
            MDC.put(SPAN_ID, txId);
        }
    }

    private static void clearCorrelationMdc() {
        MDC.remove(TRACE_ID);
        MDC.remove(TRANSACTION_ID);
        MDC.remove(SPAN_ID);
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
