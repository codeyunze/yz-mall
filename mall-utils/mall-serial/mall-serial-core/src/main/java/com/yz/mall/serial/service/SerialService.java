package com.yz.mall.serial.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.base.PageFilter;
import com.yz.mall.serial.dto.SerialAddDto;
import com.yz.mall.serial.dto.SerialQueryDto;
import com.yz.mall.serial.dto.SerialUpdateDto;
import com.yz.mall.serial.entity.SysUnqid;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 系统-流水号表(SysUnqid)表服务接口
 *
 * @author yunze
 * @since 2024-06-23 22:52:36
 */
public interface SerialService extends IService<SysUnqid> {

    /**
     * 新增数据
     *
     * @param dto 新增基础数据
     * @return 主键Id
     */
    String save(SerialAddDto dto);

    /**
     * 更新数据
     *
     * @param dto 更新基础数据
     * @return 是否操作成功
     */
    boolean cachePersistence(@Valid SerialUpdateDto dto);

    /**
     * 更新数据
     *
     * @param prefix 序列号前缀
     */
    void cachePersistence(String prefix);

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<SysUnqid> page(PageFilter<SerialQueryDto> filter);

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
     *
     * @param prefix 序列号前缀
     * @return true: 数据已经存在;    false: 数据不存在
     */
    boolean exists(String prefix);
}

