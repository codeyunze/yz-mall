package com.yz.mall.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.file.dto.YzFileInterviewDto;
import com.yz.mall.file.entity.YzSysFiles;
import io.github.codeyunze.bo.QofFileInfoBo;
import io.github.codeyunze.dto.QofFileInfoDto;

import java.util.List;

/**
 * 系统-文件表(SysFiles)表服务接口
 *
 * @author yunze
 * @since 2025-02-16 15:43:41
 */
public interface YzSysFilesService extends IService<YzSysFiles> {

    /**
     * 新增数据
     *
     * @param fileDto 新增基础数据
     * @return 主键Id
     */
    QofFileInfoBo<YzFileInterviewDto> save(QofFileInfoDto<YzFileInterviewDto> fileDto);

    /**
     * 根据文件Id查询文件信息
     *
     * @param fileId 文件Id
     * @return QofFileInfoBo 文件信息
     */
    QofFileInfoBo<YzFileInterviewDto> getFileInfoByFileId(Long fileId);

    /**
     * 根据文件Id列表获取公开的文件信息
     * @param fileIds 文件Id
     * @return 文件信息
     */
    List<QofFileInfoBo<String>> getFileInfoByFileIdsAndPublic(List<Long> fileIds);
}

