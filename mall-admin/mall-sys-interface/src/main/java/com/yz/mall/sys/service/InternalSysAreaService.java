package com.yz.mall.sys.service;

import com.yz.mall.sys.vo.InternalSysAreaVo;

/**
 * 内部开放接口: 系统管理-地区接口
 *
 * @author yunze
 * @since 2025/4/21 11:03
 */
public interface InternalSysAreaService {

    /**
     * 根据行政地区Id（行政地区编码）获取该行政地区信息
     *
     * @param id 行政地区Id（行政地区编码）
     * @return 行政地区信息
     */
    InternalSysAreaVo getById(String id);

}
