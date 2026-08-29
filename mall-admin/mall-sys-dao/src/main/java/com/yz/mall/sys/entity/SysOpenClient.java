package com.yz.mall.sys.entity;

import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 第三方开放客户端(SysOpenClient)表实体类
 *
 * @author yunze
 */
@Data
@TableName("sys_open_client")
@EqualsAndHashCode(callSuper = true)
public class SysOpenClient extends Model<SysOpenClient> {

    /**
     * 主键标识
     */
    private Long id;

    /**
     * 客户端标识，对外唯一
     */
    private String clientId;

    /**
     * 应用名称
     */
    private String clientName;

    /**
     * 联系人
     */
    private String contactName;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 状态：0禁用 1启用
     */
    private Integer status;

    /**
     * 授权到期时间，空表示长期
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime expireTime;

    /**
     * IP白名单，逗号分隔，空不限制
     */
    private String ipWhitelist;

    /**
     * 可选QPS上限
     */
    private Integer rateLimitQps;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人用户ID
     */
    private Long createId;

    /**
     * 更新人用户ID
     */
    private Long updateId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime updateTime;

    /**
     * 数据是否有效：0数据有效
     */
    @TableLogic(value = "0", delval = "current_timestamp")
    private Long invalid;

    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
