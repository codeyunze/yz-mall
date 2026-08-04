package com.yz.mall.tw.device.entity;

import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 终端档案
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tw_device")
public class TwDevice extends Model<TwDevice> {

    @TableId
    private Long id;
    /**
     * 终端客户端ID
     */
    private String deviceId;
    /**
     * 终端名称
     */
    private String deviceName;
    /**
     * 终端类型
     */
    private String deviceType;
    /**
     * MQTT 密码摘要
     */
    private String secretHash;
    /**
     * 摘要盐值
     */
    private String secretSalt;
    /**
     * 摘要算法
     */
    private String secretAlgo;
    /**
     * 状态：0禁用 1启用
     */
    private Integer status;
    /**
     * 固件版本
     */
    private String firmwareVersion;
    /**
     * 证书序列号（P2）
     */
    private String certSn;
    /**
     * 证书过期时间（P2）
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime certExpireTime;
    /**
     * 最近凭证重置时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime lastCredResetTime;
    /**
     * 备注
     */
    private String remark;
    private Long createId;
    private Long updateId;
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime createTime;
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime updateTime;
    /**
     * 逻辑删除：有效为 0；业务删除时置为行 id
     */
    @TableLogic(value = "0", delval = "1")
    private Long invalid;
}
