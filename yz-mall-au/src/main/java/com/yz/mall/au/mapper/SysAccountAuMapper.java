package com.yz.mall.au.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.au.dto.SysAccountAuQueryDto;
import com.yz.mall.au.entity.SysAccountAu;
import com.yz.mall.au.vo.SysAccountAuVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 个人黄金账户(SysAccountAu)表数据库访问层
 *
 * @author yunze
 * @since 2025-01-05 10:06:32
 */
@Mapper
public interface SysAccountAuMapper extends BaseMapper<SysAccountAu> {

    /**
     * 分页查询
     *
     * @param page   分页
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<SysAccountAuVo> selectPageByFilter(Page<Object> page, @Param("filter") SysAccountAuQueryDto filter);
}

