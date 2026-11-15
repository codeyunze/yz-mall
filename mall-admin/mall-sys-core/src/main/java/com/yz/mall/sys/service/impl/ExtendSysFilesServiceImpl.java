package com.yz.mall.sys.service.impl;

import com.yz.mall.sys.service.ExtendSysFilesService;
import io.github.codeyunze.core.QofClientFactory;
import io.github.codeyunze.metadata.FileMetadata;
import io.github.codeyunze.metadata.FileMetadataQuery;
import io.github.codeyunze.metadata.FileMetadataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 内部暴露 service 实现类：系统管理-文件接口。
 * <p>
 * 预览地址改为各存储站预签名 URL，不再拼接 {@code qof.preview-address}。
 *
 * @author yunze
 * @date 2026/1/1 星期四 22:15
 */
@Slf4j
@Service
public class ExtendSysFilesServiceImpl implements ExtendSysFilesService {

    private final QofClientFactory qofClientFactory;
    private final FileMetadataRepository metadataRepository;
    private final ObjectProvider<FileMetadataQuery> metadataQueryProvider;

    public ExtendSysFilesServiceImpl(QofClientFactory qofClientFactory,
                                     FileMetadataRepository metadataRepository,
                                     ObjectProvider<FileMetadataQuery> metadataQueryProvider) {
        this.qofClientFactory = qofClientFactory;
        this.metadataRepository = metadataRepository;
        this.metadataQueryProvider = metadataQueryProvider;
    }

    @Override
    public List<String> getFilePreviewByFileIds(List<Long> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, FileMetadata> metadataMap = loadMetadataByIds(fileIds);
        Map<String, List<Integer>> indexesByMode = new LinkedHashMap<>();
        for (int i = 0; i < fileIds.size(); i++) {
            FileMetadata metadata = metadataMap.get(fileIds.get(i));
            if (metadata == null || !StringUtils.hasText(metadata.getFileStorageMode())) {
                continue;
            }
            indexesByMode.computeIfAbsent(metadata.getFileStorageMode(), key -> new ArrayList<>()).add(i);
        }
        List<String> result = new ArrayList<>(Collections.nCopies(fileIds.size(), null));
        for (Map.Entry<String, List<Integer>> entry : indexesByMode.entrySet()) {
            List<Integer> indexes = entry.getValue();
            List<Long> ids = new ArrayList<>(indexes.size());
            for (Integer index : indexes) {
                ids.add(fileIds.get(index));
            }
            try {
                List<String> urls = qofClientFactory.buildClient(entry.getKey()).getFilePreviewByFileIds(ids);
                for (int j = 0; j < indexes.size(); j++) {
                    result.set(indexes.get(j), urls != null && j < urls.size() ? urls.get(j) : null);
                }
            } catch (Exception e) {
                log.warn("生成预览地址失败 mode={}", entry.getKey(), e);
            }
        }
        return result;
    }

    /**
     * 批量加载元数据；有查询 SPI 时走 {@code listByIds}，否则逐条查询。
     */
    private Map<Long, FileMetadata> loadMetadataByIds(List<Long> fileIds) {
        FileMetadataQuery query = metadataQueryProvider.getIfAvailable();
        Map<Long, FileMetadata> metadataMap = new HashMap<>();
        if (query != null) {
            List<FileMetadata> records = query.listByIds(fileIds);
            if (records != null) {
                for (FileMetadata metadata : records) {
                    if (metadata != null && metadata.getFileId() != null) {
                        metadataMap.put(metadata.getFileId(), metadata);
                    }
                }
            }
            return metadataMap;
        }
        for (Long fileId : fileIds) {
            if (fileId == null || metadataMap.containsKey(fileId)) {
                continue;
            }
            metadataRepository.findById(fileId).ifPresent(metadata -> metadataMap.put(fileId, metadata));
        }
        return metadataMap;
    }
}
