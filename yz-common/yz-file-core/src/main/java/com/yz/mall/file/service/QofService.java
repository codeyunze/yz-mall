package com.yz.mall.file.service;

import com.yz.mall.file.bo.QofFileInfoBo;
import com.yz.mall.file.dto.QofFileInfoDto;

/**
 * QOF文件信息操作扩展接口
 *
 * @author yunze
 * @date 2025/2/18 07:47
 */
public interface QofService {

    /**
     * 根据文件Id查询文件信息
     *
     * @param fileId 文件Id
     */
    QofFileInfoBo getFileInfoByFileId(Long fileId);

    /**
     * 文件上传之前
     *
     * @param fileDto 新增文件基础数据
     * @return 主键Id, 如果上传失败则返回null
     */
    Long beforeUpload(QofFileInfoDto fileDto);

    /**
     * 文件上传之后
     *
     * @param fileDto 文件基础数据
     * @return 主键Id, 如果上传失败则返回null
     */
    QofFileInfoBo afterUpload(QofFileInfoDto fileDto);

    /**
     * 下载前执行操作
     *
     * @param fileId 文件Id
     */
    void beforeDownload(Long fileId);

    /**
     * 下载后执行操作
     *
     * @param fileId 文件Id
     */
    void afterDownload(Long fileId);

    /**
     * 文件删除前执行操作
     * @param fileId 删除
     */
    boolean beforeDelete(Long fileId);
}
