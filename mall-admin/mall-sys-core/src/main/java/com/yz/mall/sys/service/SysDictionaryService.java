package com.yz.mall.sys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.base.PageFilter;
import com.yz.mall.sys.dto.SysDictionaryAddDto;
import com.yz.mall.sys.dto.SysDictionaryQueryDto;
import com.yz.mall.sys.dto.SysDictionaryUpdateDto;
import com.yz.mall.sys.entity.SysDictionary;
import com.yz.mall.sys.vo.ExtendSysDictionaryVo;
import jakarta.validation.Valid;


/**
 * 系统字典表(SysDictionary)表服务接口
 *
 * @author yunze
 * @since 2025-11-30 09:53:52
 */
public interface SysDictionaryService extends IService<SysDictionary> {

    /**
     * 新增数据
     *
     * @param dto 新增基础数据
     * @return 主键Id
     */
    Long save(SysDictionaryAddDto dto);

    /**
     * 更新数据
     *
     * @param dto 更新基础数据
     * @return 是否操作成功
     */
    boolean update(@Valid SysDictionaryUpdateDto dto);

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<ExtendSysDictionaryVo> page(PageFilter<SysDictionaryQueryDto> filter);

    /**
     * 根据数据字典 Key 获取数据字典信息
     * @param key 数据字典 Key
     * @return 数据字典-树形结构
     */
    ExtendSysDictionaryVo getByKey(String key);

}

