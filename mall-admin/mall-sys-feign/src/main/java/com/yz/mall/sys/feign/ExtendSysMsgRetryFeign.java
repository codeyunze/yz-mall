package com.yz.mall.sys.feign;

import com.yz.mall.base.Result;
import com.yz.mall.sys.dto.MsgRetryRecordDto;
import com.yz.mall.web.configuration.OpenFeignConfig;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 内部暴露接口: 消息重试
 *
 * @author yunze
 * @since 2025-01-20
 */
@FeignClient(name = "mall-sys", contextId = "extendSysMsgRetry", path = "extend/sys/msg/retry", configuration = OpenFeignConfig.class)
public interface ExtendSysMsgRetryFeign {

    /**
     * 记录消息消费失败
     *
     * @param dto 消息重试记录DTO
     * @return 记录ID
     */
    @PostMapping("recordFailure")
    Result<Long> recordFailure(@RequestBody @Valid MsgRetryRecordDto dto);

    /**
     * 根据消息ID查询记录状态（用于幂等性判断）
     *
     * @param msgId 消息ID
     * @return 消息重试记录状态码（0重试中/1待处理/2已处理/3忽略），如果不存在返回null
     */
    @GetMapping("getStatusByMsgId/{msgId}")
    Result<Integer> getStatusByMsgId(@PathVariable String msgId);

    /**
     * 根据消息ID标记消息为已处理
     *
     * @param msgId 消息ID
     * @return 是否成功
     */
    @PostMapping("markAsProcessedByMsgId/{msgId}")
    Result<Boolean> markAsProcessedByMsgId(@PathVariable String msgId);
}
