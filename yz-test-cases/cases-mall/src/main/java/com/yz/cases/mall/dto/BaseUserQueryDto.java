package com.yz.cases.mall.dto;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 基础-用户(BaseUser)表实体类
 *
 * @author yunze
 * @since 2024-06-11 23:16:13
 */
@Data
public class BaseUserQueryDto extends Model<BaseUserQueryDto> {

    private static final long serialVesionUID = 1L;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;
}

