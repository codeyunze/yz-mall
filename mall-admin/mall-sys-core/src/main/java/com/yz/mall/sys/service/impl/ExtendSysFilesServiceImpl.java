package com.yz.mall.sys.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.yz.mall.sys.SysProperties;
import com.yz.mall.sys.service.ExtendSysFilesService;
import io.github.codeyunze.core.QofClientFactory;
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


    private final QofClientFactory qofClientFactory;

    private final SysProperties sysProperties;

    public ExtendSysFilesServiceImpl(QofClientFactory qofClientFactory, SysProperties sysProperties) {
        this.qofClientFactory = qofClientFactory;
        this.sysProperties = sysProperties;

    }

    @Override
    public List<String> getFilePreviewByFileIds(List<Long> fileIds) {
        List<String> preview = qofClientFactory.buildClient(sysProperties.getStorage().getMode()).getFilePreviewByFileIds(fileIds);
        if (preview == null || preview.isEmpty()) {
            return new ArrayList<>();
        }
        String tokenValue = StpUtil.getTokenInfo().getTokenValue();
        List<String> result = new ArrayList<>();
        preview.forEach(f -> {
            if (f != null) {
                result.add(f + "?token=" + tokenValue);
            }
        });
        return result;
    }
}
