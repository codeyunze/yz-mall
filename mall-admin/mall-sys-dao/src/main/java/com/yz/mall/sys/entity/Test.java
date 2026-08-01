package com.yz.mall.sys.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * (Test)表实体类
 *
 * @author yunze
 * @since 2026-04-20 19:23:22
 */
@Data
public class Test extends Model<Test> {

    private Integer id;

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

