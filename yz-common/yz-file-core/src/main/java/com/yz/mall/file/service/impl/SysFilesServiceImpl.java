package com.yz.mall.file.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.file.bo.QofFileInfoBo;
import com.yz.mall.file.dto.QofFileInfoDto;
import com.yz.mall.file.entity.SysFiles;
import com.yz.mall.file.mapper.SysFilesMapper;
import com.yz.mall.file.service.SysFilesService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 系统-文件表(SysFiles)表服务实现类
 *
 * @author yunze
 * @since 2025-02-16 15:43:41
 */
@Service
public class SysFilesServiceImpl extends ServiceImpl<SysFilesMapper, SysFiles> implements SysFilesService {

    @Override
    public QofFileInfoBo save(QofFileInfoDto fileDto) {
        SysFiles fileDo = new SysFiles();
        BeanUtils.copyProperties(fileDto, fileDo);
        if (null == fileDto.getFileId()) {
            fileDo.setId(IdUtil.getSnowflakeNextId());
        } else {
            fileDo.setId(fileDto.getFileId());
        }
        baseMapper.insert(fileDo);
        QofFileInfoBo fileBo = new QofFileInfoBo();
        BeanUtils.copyProperties(fileDo, fileBo);
        return fileBo;
    }

    @Override
    public QofFileInfoBo getByFileId(Long fileId) {
        return baseMapper.selectByFileId(fileId);
    }

    @Override
    public boolean deleteByFileId(Long fileId) {
        return baseMapper.deleteById(fileId) > 0;
    }
}

