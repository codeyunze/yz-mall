package com.yz.mall.file.service;

import com.yz.mall.file.dto.QofFileInfoDto;

/**
 * QOF文件信息入库操作接口
 *
 * @author yunze
 * @date 2025/2/18 07:47
 */
public interface QofService {

    /**
     * 新增文件数据
     *
     * @param dto 新增文件基础数据
     * @return 主键Id
     */
    Long save(QofFileInfoDto dto);
}
