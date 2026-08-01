package com.yz.mall.sys.utils;

import com.yz.mall.base.exception.BusinessException;

/**
 * 异常重试判断工具类
 * 用于判断异常是否可重试
 *
 * @author yunze
 * @since 2025-01-20
 */
public class ExceptionRetryUtil {

    /**
     * 判断异常是否可重试
     *
     * @param exception 异常对象
     * @return true-可重试，false-不可重试
     */
    public static boolean isRetryable(Throwable exception) {
        if (exception == null) {
            return false;
        }

        // 业务异常通常不可重试（除非是临时性业务异常）
        if (exception instanceof BusinessException) {
            // 可以根据业务异常的具体信息判断，这里默认不可重试
            return false;
        }

        // 网络相关异常，可重试
        if (exception instanceof java.net.ConnectException
                || exception instanceof java.net.SocketTimeoutException
                || exception.getCause() instanceof java.net.ConnectException
                || exception.getCause() instanceof java.net.SocketTimeoutException) {
            return true;
        }

        // 数据库连接超时异常，可重试
        if (exception instanceof java.sql.SQLException) {
            java.sql.SQLException sqlException = (java.sql.SQLException) exception;
            String sqlState = sqlException.getSQLState();
            // SQL状态码以08开头的表示连接异常，可重试
            if (sqlState != null && sqlState.startsWith("08")) {
                return true;
            }
        }

        // 数据库超时异常，可重试
        if (exception instanceof java.sql.SQLTimeoutException) {
            return true;
        }

        // 其他运行时异常，根据异常类型判断
        if (exception instanceof RuntimeException) {
            String message = exception.getMessage();
            if (message != null) {
                String lowerMessage = message.toLowerCase();
                // 包含这些关键词的异常，可能是临时性异常，可重试
                if (lowerMessage.contains("timeout")
                        || lowerMessage.contains("connection")
                        || lowerMessage.contains("network")
                        || lowerMessage.contains("temporary")
                        || lowerMessage.contains("retry")) {
                    return true;
                }
            }
        }

        // 默认不可重试
        return false;
    }

    /**
     * 获取异常类型名称
     *
     * @param exception 异常对象
     * @return 异常类型名称
     */
    public static String getExceptionType(Throwable exception) {
        if (exception == null) {
            return "Unknown";
        }
        return exception.getClass().getSimpleName();
    }

    /**
     * 获取异常错误码（如果有）
     *
     * @param exception 异常对象
     * @return 错误码
     */
    public static String getErrorCode(Throwable exception) {
        if (exception instanceof java.sql.SQLException) {
            java.sql.SQLException sqlException = (java.sql.SQLException) exception;
            return String.valueOf(sqlException.getErrorCode());
        }
        return null;
    }
}

