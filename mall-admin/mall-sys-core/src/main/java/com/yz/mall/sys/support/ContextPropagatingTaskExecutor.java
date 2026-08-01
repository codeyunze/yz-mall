package com.yz.mall.sys.support;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 在提交入口透传 {@link RequestContextHolder} 与 MDC。
 * <p>
 * Dynamic TP 会把 Spring 内部 {@code ThreadPoolExecutor} 替换为 {@code ThreadPoolExecutorProxy}，
 * 导致写在内部执行器上的 {@link TaskDecorator} 失效；因此必须在本类的 {@code execute}/{@code submit}
 * 中捕获上下文，再交给底层池执行。
 * <p>
 * 不再覆盖已废弃的 {@code submitListenable}；Spring 6 起应使用 {@link #execute} / {@link #submit}。
 */
public class ContextPropagatingTaskExecutor extends ThreadPoolTaskExecutor {

    @Override
    public void execute(Runnable task) {
        super.execute(wrap(task));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(wrap(task));
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(wrap(task));
    }

    private Runnable wrap(Runnable task) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        return () -> {
            try {
                if (requestAttributes != null) {
                    RequestContextHolder.setRequestAttributes(requestAttributes);
                }
                if (mdcContext != null) {
                    MDC.setContextMap(mdcContext);
                }
                task.run();
            } finally {
                RequestContextHolder.resetRequestAttributes();
                MDC.clear();
            }
        };
    }

    private <T> Callable<T> wrap(Callable<T> task) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        return () -> {
            try {
                if (requestAttributes != null) {
                    RequestContextHolder.setRequestAttributes(requestAttributes);
                }
                if (mdcContext != null) {
                    MDC.setContextMap(mdcContext);
                }
                return task.call();
            } finally {
                RequestContextHolder.resetRequestAttributes();
                MDC.clear();
            }
        };
    }
}
