package com.yz.mall.oms.config;

import com.yz.mall.sys.AbstractSysPendingTasksQueueConfig;
import org.springframework.context.annotation.Configuration;

/**
 * 订单管理-退款审核待办 RocketMQ 配置。
 * <p>
 * taskCode {@code OMS:ORDER:REFUND} 经路由转换后对应 Tag 前缀 {@code oms_order_refund}。
 */
@Configuration
public class OmsOrderRefundQueueConfig extends AbstractSysPendingTasksQueueConfig {

    /**
     * 待办任务标识（写入 SysPendingTasks.taskCode）
     */
    public static final String TASK_CODE = "OMS:ORDER:REFUND";

    /**
     * 待办任务队列名称（业务标识）
     */
    public static final String QUEUE_NAME = "oms_order_refund";

    /**
     * 开始处理 Tag
     */
    public static final String START_TAG = QUEUE_NAME + "_start_key";

    /**
     * 结束处理 Tag
     */
    public static final String END_TAG = QUEUE_NAME + "_end_key";
}
