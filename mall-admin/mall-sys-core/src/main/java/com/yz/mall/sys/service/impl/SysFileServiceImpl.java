package com.yz.mall.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.exception.DataNotExistException;
import com.yz.mall.sys.dto.SysFileQueryDto;
import com.yz.mall.sys.dto.SysFileUpdateDto;
import com.yz.mall.sys.service.SysFileService;
import com.yz.mall.sys.vo.SysFileVo;
import io.github.codeyunze.entity.SysFiles;
import io.github.codeyunze.mapper.FilesMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统文件表(SysFiles)表服务实现类
 * <p>
 * 注意：物理文件的上传、下载、删除等操作请使用qof-core和qof-web提供的工具
 * 本Service仅提供文件数据的管理操作
 *
 * @author yunze
 * @date 2025/12/21 星期日 14:35
 */
@Service
public class SysFileServiceImpl extends ServiceImpl<FilesMapper, SysFiles> implements SysFileService {

    @Override
    public boolean update(SysFileUpdateDto dto) {
        SysFiles sysFiles = baseMapper.selectById(dto.getId());
        if (sysFiles == null) {
            throw new DataNotExistException("文件不存在");
        }

        SysFiles updateEntity = new SysFiles();
        BeanUtils.copyProperties(dto, updateEntity);
        updateEntity.setUpdateTime(LocalDateTime.now());
        
        return baseMapper.updateById(updateEntity) > 0;
    }

    @Override
    public Page<SysFileVo> page(PageFilter<SysFileQueryDto> filter) {
        LambdaQueryWrapper<SysFiles> queryWrapper = new LambdaQueryWrapper<>();
        
        if (filter.getFilter() != null) {
            SysFileQueryDto queryDto = filter.getFilter();
            queryWrapper.eq(queryDto.getId() != null, SysFiles::getId, queryDto.getId());
            queryWrapper.like(StringUtils.hasText(queryDto.getFileName()), SysFiles::getFileName, queryDto.getFileName());
            queryWrapper.eq(StringUtils.hasText(queryDto.getFileType()), SysFiles::getFileType, queryDto.getFileType());
            queryWrapper.eq(StringUtils.hasText(queryDto.getFileStorageMode()), SysFiles::getFileStorageMode, queryDto.getFileStorageMode());
            queryWrapper.eq(StringUtils.hasText(queryDto.getFileStorageStation()), SysFiles::getFileStorageStation, queryDto.getFileStorageStation());
            queryWrapper.ge(queryDto.getCreateTimeFrom() != null, SysFiles::getCreateTime, queryDto.getCreateTimeFrom());
            queryWrapper.le(queryDto.getCreateTimeTo() != null, SysFiles::getCreateTime, queryDto.getCreateTimeTo());
        }
        
        queryWrapper.orderByDesc(SysFiles::getCreateTime);
        
        Page<SysFiles> page = baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
        
        if (page.getTotal() == 0) {
            return new Page<>();
        }

        List<SysFileVo> voList = page.getRecords().stream().map(sysFiles -> {
            SysFileVo vo = new SysFileVo();
            BeanUtils.copyProperties(sysFiles, vo);
            // 预览地址应该使用qof-web提供的接口
            return vo;
        }).collect(Collectors.toList());

        Page<SysFileVo> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(voList);
        return result;
    }

    @Override
    public SysFileVo getById(Long id) {
        SysFiles sysFiles = baseMapper.selectById(id);
        if (sysFiles == null) {
            throw new DataNotExistException("文件不存在");
        }

        SysFileVo vo = new SysFileVo();
        BeanUtils.copyProperties(sysFiles, vo);
        return vo;
    }

    @Override
    public boolean removeById(Long id) {
        SysFiles sysFiles = baseMapper.selectById(id);
        if (sysFiles == null) {
            return true;
        }

        // 仅删除数据库记录，物理文件删除请使用qof工具
        return baseMapper.deleteById(id) > 0;
    }
}
