package com.yz.mall.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.sys.dto.SysLoginLogQueryDto;
import com.yz.mall.sys.entity.SysLoginLog;
import com.yz.mall.sys.vo.SysLoginLogVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 系统-登录日志(SysLoginLog)表数据库访问层
 *
 * @author yunze
 * @since 2025-12-11
 */
@Mapper
public interface SysLoginLogMapper extends BaseMapper<SysLoginLog> {

    /**
     * 分页查询
     *
     * @param page   分页参数
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<SysLoginLogVo> selectPage(Page<Object> page, @Param("filter") SysLoginLogQueryDto filter);
}

