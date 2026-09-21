package com.yz.mall.sys.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.base.exception.DataNotExistException;
import com.yz.mall.sys.dto.SysFileQueryDto;
import com.yz.mall.sys.dto.SysFileUpdateDto;
import com.yz.mall.sys.service.SysFileService;
import com.yz.mall.sys.vo.SysFileVo;
import io.github.codeyunze.metadata.FileMetadata;
import io.github.codeyunze.metadata.FileMetadataQuery;
import io.github.codeyunze.metadata.FileMetadataQueryCriteria;
import io.github.codeyunze.metadata.FileMetadataRepository;
import io.github.codeyunze.metadata.PageResult;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统文件元数据服务。
 * <p>
 * 持久化走 QOF {@link FileMetadataRepository} / {@link FileMetadataQuery}，不再直接依赖表实体。
 *
 * @author yunze
 * @date 2025/12/21 星期日 14:35
 */
@Service
public class SysFileServiceImpl implements SysFileService {

    private final FileMetadataRepository metadataRepository;
    private final ObjectProvider<FileMetadataQuery> metadataQueryProvider;

    public SysFileServiceImpl(FileMetadataRepository metadataRepository,
                              ObjectProvider<FileMetadataQuery> metadataQueryProvider) {
        this.metadataRepository = metadataRepository;
        this.metadataQueryProvider = metadataQueryProvider;
    }

    @Override
    public boolean update(SysFileUpdateDto dto) {
        FileMetadata metadata = metadataRepository.findById(dto.getId())
                .orElseThrow(() -> new DataNotExistException("文件不存在"));
        if (StringUtils.hasText(dto.getFileName())) {
            metadata.setFileName(dto.getFileName().trim());
        }
        if (StringUtils.hasText(dto.getFileStorageMode())) {
            metadata.setFileStorageMode(dto.getFileStorageMode().trim());
        }
        if (StringUtils.hasText(dto.getFileStorageStation())) {
            metadata.setFileStorageStation(dto.getFileStorageStation().trim());
        }
        metadata.setUpdateTime(LocalDateTime.now());
        metadataRepository.update(metadata);
        return true;
    }

    @Override
    public Page<SysFileVo> page(PageFilter<SysFileQueryDto> filter) {
        SysFileQueryDto queryDto = filter.getFilter();
        if (queryDto != null && queryDto.getId() != null) {
            return pageById(queryDto.getId());
        }
        FileMetadataQuery query = metadataQueryProvider.getIfAvailable();
        if (query == null) {
            throw new BusinessException("未提供 FileMetadataQuery，文件列表不可用。请引入 qof-persistence-mysql");
        }
        FileMetadataQueryCriteria criteria = new FileMetadataQueryCriteria();
        criteria.setPageNum(filter.getCurrent());
        criteria.setPageSize(filter.getSize());
        if (queryDto != null) {
            criteria.setFileName(queryDto.getFileName());
            criteria.setFileStorageMode(queryDto.getFileStorageMode());
            criteria.setFileStorageStation(queryDto.getFileStorageStation());
        }
        PageResult<FileMetadata> page = query.page(criteria);
        List<SysFileVo> records = page.getRecords() == null ? Collections.emptyList()
                : page.getRecords().stream().map(this::toVo).collect(Collectors.toList());
        Page<SysFileVo> result = new Page<>(page.getPageNum(), page.getPageSize(), page.getTotal());
        result.setRecords(records);
        return result;
    }

    @Override
    public SysFileVo getById(Long id) {
        FileMetadata metadata = metadataRepository.findById(id)
                .orElseThrow(() -> new DataNotExistException("文件不存在"));
        return toVo(metadata);
    }

    @Override
    public boolean removeById(Long id) {
        if (metadataRepository.findById(id).isEmpty()) {
            return true;
        }
        return metadataRepository.deleteById(id);
    }

    /**
     * 按主键精确查询，包装成分页结果。
     */
    private Page<SysFileVo> pageById(Long id) {
        Page<SysFileVo> result = new Page<>(1, 1, 0);
        metadataRepository.findById(id).ifPresent(metadata -> {
            result.setTotal(1);
            result.setRecords(List.of(toVo(metadata)));
        });
        return result;
    }

    /**
     * 元数据转为对外 VO。列表查询默认不含存储路径。
     */
    private SysFileVo toVo(FileMetadata metadata) {
        SysFileVo vo = new SysFileVo();
        vo.setId(metadata.getFileId());
        vo.setCreateTime(metadata.getCreateTime());
        vo.setUpdateTime(metadata.getUpdateTime());
        vo.setFileName(metadata.getFileName());
        vo.setFilePath(metadata.getFilePath());
        vo.setFileType(metadata.getFileType());
        vo.setFileSize(metadata.getFileSize());
        vo.setFileStorageStation(metadata.getFileStorageStation());
        vo.setFileStorageMode(metadata.getFileStorageMode());
        return vo;
    }
}
