package com.yz.cases.mall.dto;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;

/**
 * 系统-数据源信息(SysDatasource)表查询过滤条件数据模型类
 *
 * @author yunze
 * @since 2024-06-12 11:02:09
 */
@Data
public class SysDatasourceQueryDto implements Serializable {

    private static final long serialVesionUID = 1L;


    /**
     * 主键标识
     */
    private Long id;

    /**
     * 创建人
     */
    private Long createId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    private Long updateId;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

    /**
     * 数据是否有效：0数据有效
     */
    private Integer invalid;

    /**
     * 数据源访问用户名
     */
    private String username;

    /**
     * 数据源访问密码
     */
    private String password;

    /**
     * 数据源驱动类
     */
    private String driverClassName;

    /**
     * 数据源访问地址
     */
    private String url;

}

