package com.yz.cases.mall.dto;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 基础-用户(BaseUser)表实体类
 *
 * @author yunze
 * @since 2024-06-11 23:16:13
 */
@Data
public class BaseUserUpdateDto extends Model<BaseUserUpdateDto> {

    private static final long serialVesionUID = 1L;

    /**
     * 主键ID
     */
    @NotNull(message = "主键不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 密码
     */
    private String password;

}

