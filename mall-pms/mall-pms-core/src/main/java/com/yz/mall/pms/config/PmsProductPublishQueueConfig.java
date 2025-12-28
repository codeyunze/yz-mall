package com.yz.mall.pms.config;

import com.yz.mall.sys.AbstractSysPendingTasksQueueConfig;
import org.springframework.context.annotation.Configuration;

/**
 * 商品管理-商品上架待办 RocketMQ 配置
 *
 * 说明：
 * - 原实现基于 RabbitMQ，声明 Queue 和 Binding
 * - 当前实现切换为 RocketMQ，仅保留队列（实际为业务标识）常量
 * - RocketMQ 中通过 Topic + Tag 实现路由，具体发送逻辑在 {@link com.yz.mall.sys.service.impl.SysPendingTasksServiceImpl}
 *
 * @author yunze
 * @date 2025/1/21 22:42
 */
@Configuration
public class PmsProductPublishQueueConfig extends AbstractSysPendingTasksQueueConfig {

    /**
     * 待办任务队列名称（业务标识）
     * 对应 SysPendingTasks.taskCode 字段
     */
    public static final String QUEUE_NAME = "pms_product_publish";

    /**
     * 开始处理 Tag
     */
    public static final String START_TAG = QUEUE_NAME + "_start_key";

    /**
     * 结束处理 Tag
     */
    public static final String END_TAG = QUEUE_NAME + "_end_key";

}
