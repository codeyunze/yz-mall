package com.yz.mall.pms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.pms.dto.PmsShopCartAddDto;
import com.yz.mall.pms.dto.PmsShopCartQueryDto;
import com.yz.mall.pms.dto.PmsShopCartUpdateDto;
import com.yz.mall.pms.entity.PmsShopCart;
import com.yz.mall.pms.vo.PmsShopCartVo;
import com.yz.mall.web.common.PageFilter;

import javax.validation.Valid;
import java.util.List;

/**
 * 购物车数据表(PmsShopCart)表服务接口
 *
 * @author yunze
 * @since 2025-01-24 10:08:17
 */
public interface PmsShopCartService extends IService<PmsShopCart> {

    /**
     * 新增数据
     *
     * @param dto 新增基础数据
     * @return 主键Id
     */
    Long save(PmsShopCartAddDto dto);

    /**
     * 更新数据
     *
     * @param dto 更新基础数据
     * @return 是否操作成功
     */
    boolean update(@Valid PmsShopCartUpdateDto dto);

    /**
     * 删除购物车商品
     *
     * @param ids     购物车信息Id
     * @param userId 用户Id
     * @return 是否操作成功
     */
    boolean removeByIds(List<Long> ids, Long userId);

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<PmsShopCartVo> page(PageFilter<PmsShopCartQueryDto> filter);

}

