package com.yz.auth.baseClient.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 基础-客户端表
 * </p>
 *
 * @author gaohan
 * @since 2022-10-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BaseClient implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 主键自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 客户端名称
     */
    private String clientName;

    /**
     * 客户端编号
     */
    private String clientId;

    /**
     * 客户端密钥
     */
    private String clientSecret;

    /**
     * 客户端重定向地址
     */
    private String redirectUrl;

    /**
     * 数据无效的，0:数据有效,1:数据无效
     */
    private Boolean invalid;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 更新时间
     */
    private LocalDateTime updateDate;


}
