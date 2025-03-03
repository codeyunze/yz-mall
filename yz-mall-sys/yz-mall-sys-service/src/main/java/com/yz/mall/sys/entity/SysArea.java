package com.yz.mall.sys.entity;

import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * (SysArea)表实体类
 *
 * @author yunze
 * @since 2025-03-03 22:38:55
 */
@Data
public class SysArea extends Model<SysArea> {

    private String id;

    private String name;

    /**
     * 父类Code
     */
    private String parentId;

    /**
     * 等级
     */
    private Integer levelType;

    private String parentPath;

    private String parentName;

    /**
     * 排序
     */
    private Integer sortNum;

    /**
     * 状态：0禁用，1正常
     */
    private Integer status;

    /**
     * 是否已删除：0正常，1已删除
     */
    @TableLogic(value = "0", delval = "current_timestamp")
    private Integer invalid;

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

