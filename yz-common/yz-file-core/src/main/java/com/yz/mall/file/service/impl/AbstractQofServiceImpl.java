package com.yz.mall.file.service.impl;

import com.yz.mall.file.QofProperties;
import com.yz.mall.file.bo.QofFileInfoBo;
import com.yz.mall.file.dto.QofFileInfoDto;
import com.yz.mall.file.service.QofService;
import com.yz.mall.file.service.SysFilesService;
import com.yz.mall.web.exception.DataNotExistException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * QOF文件信息操作扩展接口默认实现
 * @author yunze
 * @date 2025/2/18 07:49
 */
@Slf4j
@Service
public abstract class AbstractQofServiceImpl implements QofService {

    private final SysFilesService filesService;

    public AbstractQofServiceImpl(SysFilesService filesService) {
        this.filesService = filesService;
    }

    @Resource
    private QofProperties qofProperties;

    @Override
    public Long beforeUpload(QofFileInfoDto fileDto) {
        log.info("文件上传前执行");
        return fileDto.getFileId();
    }

    @Override
    public QofFileInfoBo afterUpload(QofFileInfoDto fileDto) {
        log.info("文件上传后执行");
        if (qofProperties.isPersistentEnable()) {
            return filesService.save(fileDto);
        }
        QofFileInfoBo fileBo = new QofFileInfoBo();
        BeanUtils.copyProperties(fileDto, fileBo);
        return fileBo;
    }

    @Override
    public QofFileInfoBo beforeDownload(Long fileId) {
        log.info("文件下载前执行");
        QofFileInfoBo fileBo = filesService.getByFileId(fileId);
        if (fileBo == null) {
            throw new DataNotExistException("文件信息不存在");
        }
        return fileBo;
    }

    @Override
    public void afterDownload(Long fileId) {
        log.info("文件下载后执行");
    }
}
