package com.yz.mall.sys.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.yz.mall.sys.service.ExtendSysFilesService;
import io.github.codeyunze.service.FilesService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 内部暴露service实现类: 系统管理-文件接口
 * @author yunze
 * @date 2026/1/1 星期四 22:15
 */
@Service
public class ExtendSysFilesServiceImpl implements ExtendSysFilesService {

    private final FilesService filesService;

    public ExtendSysFilesServiceImpl(FilesService filesService) {
        this.filesService = filesService;
    }

    @Override
    public List<String> getFilePreviewByFileIds(List<Long> fileIds) {
        List<String> preview = filesService.getFilePreviewByFileId(fileIds);
        String tokenValue = StpUtil.getTokenInfo().getTokenValue();
        List<String> result = new ArrayList<>();
        preview.forEach(f -> {
            result.add(f + "?token=" + tokenValue);
        });
        return result;
    }
}
