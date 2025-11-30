package com.yz.mall.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.sys.entity.SysArea;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * (SysArea)表数据库访问层
 *
 * @author yunze
 * @since 2025-03-03 22:38:56
 */
@Mapper
public interface SysAreaMapper extends BaseMapper<SysArea> {

    /**
     * 保持数据库连接
     */
    @Select("select 1 from dual")
    Integer keepalive();

}

