package com.yz.unqid.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.unqid.dto.SysUnqidAddDto;
import com.yz.unqid.dto.SysUnqidQueryDto;
import com.yz.unqid.dto.SysUnqidUpdateDto;
import com.yz.unqid.entity.SysUnqid;
import com.yz.tools.PageFilter;

import javax.validation.Valid;
import java.util.List;

/**
 * 系统-序列号表(SysUnqid)表服务接口
 *
 * @author yunze
 * @since 2024-06-23 22:52:36
 */
public interface SysUnqidService extends IService<SysUnqid> {

    /**
     * 新增数据
     *
     * @param dto 新增基础数据
     * @return 主键Id
     */
    String save(SysUnqidAddDto dto);

    /**
     * 更新数据
     *
     * @param dto 更新基础数据
     * @return 是否操作成功
     */
    boolean update(@Valid SysUnqidUpdateDto dto);

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<SysUnqid> page(PageFilter<SysUnqidQueryDto> filter);

    /**
     * 生成流水号
     *
     * @param prefix       流水号前缀
     * @param numberLength 流水号的序号长度
     * @return 流水号
     */
    String generateSerialNumber(String prefix, Integer numberLength);

    /**
     * 批量生成流水号
     *
     * @param prefix       流水号前缀
     * @param numberLength 流水号的序号长度
     * @param quantity     生成流水号数量
     * @return 流水号列表
     */
    List<String> generateSerialNumbers(String prefix, Integer numberLength, Integer quantity);

    /**
     * 数据是否存在
     * @param prefix 序列号前缀
     * @return true: 数据已经存在;    false: 数据不存在
     */
    boolean exists(String prefix);
}

