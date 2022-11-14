package com.yz.auth.baseUser.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 基础-用户表
 * </p>
 *
 * @author gaohan
 * @since 2022-09-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BaseUser implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 主键自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户唯一id
     */
    private String userId;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 名称
     */
    private String userName;

    /**
     * 更新时间
     */
    private LocalDateTime updateDate;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;


}
