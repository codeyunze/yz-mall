package com.yz.mall.file.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.file.dto.YzFileInterviewDto;
import com.yz.mall.file.entity.YzSysFiles;
import com.yz.mall.file.mapper.YzSysFilesMapper;
import com.yz.mall.file.service.YzSysFilesService;
import io.github.codeyunze.bo.QofFileInfoBo;
import io.github.codeyunze.dto.QofFileInfoDto;
import io.github.codeyunze.exception.DataNotExistException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 系统-文件表(SysFiles)表服务实现类
 *
 * @author yunze
 * @since 2025-02-16 15:43:41
 */
@Service
public class YzSysFilesServiceImpl extends ServiceImpl<YzSysFilesMapper, YzSysFiles> implements YzSysFilesService {

    @Override
    public QofFileInfoBo<YzFileInterviewDto> save(QofFileInfoDto<YzFileInterviewDto> fileDto) {
        YzSysFiles fileDo = new YzSysFiles();
        BeanUtils.copyProperties(fileDto, fileDo);
        if (null == fileDto.getFileId()) {
            fileDo.setId(IdUtil.getSnowflakeNextId());
        } else {
            fileDo.setId(fileDto.getFileId());
        }
        fileDo.setCreateId(fileDto.getExtendObject().getCreateId());
        fileDo.setPublicAccess(fileDto.getExtendObject().getPublicAccess());
        baseMapper.insert(fileDo);
        QofFileInfoBo<YzFileInterviewDto> fileBo = new QofFileInfoBo<>();
        BeanUtils.copyProperties(fileDo, fileBo);
        return fileBo;
    }

    @Override
    public QofFileInfoBo<YzFileInterviewDto> getFileInfoByFileId(Long fileId) {
        QofFileInfoBo<YzFileInterviewDto> fileBo = baseMapper.selectByFileId(fileId);
        if (fileBo == null) {
            throw new DataNotExistException("文件信息不存在");
        }
        YzFileInterviewDto interviewDto = baseMapper.selectFileOwnerByFileId(fileId);
        fileBo.setExtendObject(interviewDto);
        return fileBo;
    }
}

