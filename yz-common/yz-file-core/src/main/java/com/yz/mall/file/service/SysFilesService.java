package com.yz.mall.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.file.bo.QofFileInfoBo;
import com.yz.mall.file.dto.QofFileInfoDto;
import com.yz.mall.file.entity.SysFiles;

/**
 * 系统-文件表(SysFiles)表服务接口
 *
 * @author yunze
 * @since 2025-02-16 15:43:41
 */
public interface SysFilesService extends IService<SysFiles> {

    /**
     * 新增数据
     *
     * @param fileDto 新增基础数据
     * @return 主键Id
     */
    QofFileInfoBo save(QofFileInfoDto fileDto);

    /**
     * 根据文件Id获取文件信息
     *
     * @param fileId 文件Id
     * @return 文件基础信息
     */
    QofFileInfoBo getByFileId(Long fileId);

}

