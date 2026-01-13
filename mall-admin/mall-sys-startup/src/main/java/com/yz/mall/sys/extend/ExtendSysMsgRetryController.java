package com.yz.mall.sys.extend;

import com.yz.mall.base.ApiController;
import com.yz.mall.base.Result;
import com.yz.mall.sys.dto.MsgRetryRecordDto;
import com.yz.mall.sys.service.ExtendSysMsgRetryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 内部暴露接口: 消息重试
 *
 * @author yunze
 * @since 2025-01-20
 */
@Slf4j
@RestController
@RequestMapping("extend/sys/msg/retry")
public class ExtendSysMsgRetryController extends ApiController {

    private final ExtendSysMsgRetryService extendSysMsgRetryService;

    public ExtendSysMsgRetryController(ExtendSysMsgRetryService extendSysMsgRetryService) {
        this.extendSysMsgRetryService = extendSysMsgRetryService;
    }

    /**
     * 记录消息消费失败
     *
     * @param dto 消息重试记录DTO
     * @return 记录ID
     */
    @PostMapping("recordFailure")
    public Result<Long> recordFailure(@RequestBody @Valid MsgRetryRecordDto dto) {
        Long id = extendSysMsgRetryService.recordFailure(dto);
        return success(id);
    }

    /**
     * 根据消息ID查询记录状态（用于幂等性判断）
     *
     * @param msgId 消息ID
     * @return 消息重试记录状态码（0重试中/1待处理/2已处理/3忽略），如果不存在返回null
     */
    @GetMapping("getStatusByMsgId/{msgId}")
    public Result<Integer> getStatusByMsgId(@PathVariable String msgId) {
        Integer status = extendSysMsgRetryService.getStatusByMsgId(msgId);
        return success(status);
    }

    /**
     * 根据消息ID标记消息为已处理
     *
     * @param msgId 消息ID
     * @return 是否成功
     */
    @PostMapping("markAsProcessedByMsgId/{msgId}")
    public Result<Boolean> markAsProcessedByMsgId(@PathVariable String msgId) {
        boolean success = extendSysMsgRetryService.markAsProcessedByMsgId(msgId);
        return success(success);
    }
}
