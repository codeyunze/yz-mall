package com.yz.mall.sys.vo;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 应用配置(SysApplication)表实体类
 *
 * @author yunze
 * @since 2024-08-11 20:10:14
 */
@Data
public class SysApplicationVo extends Model<SysApplicationVo> {

    /**
     * 主键标识
     */
    private String id;

    /**
     * 创建人
     */
    private String createdId;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新人
     */
    private String updatedId;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

    /**
     * 应用id
     */
    private String clientId;

    /**
     * 应用密钥
     */
    private String clientSecret;

    /**
     * 应用名称
     */
    private String clientName;

    /**
     * 备注说明信息
     */
    private String remark;

}

