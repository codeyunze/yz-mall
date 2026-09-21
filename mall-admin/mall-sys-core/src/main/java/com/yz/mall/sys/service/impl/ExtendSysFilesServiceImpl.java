package com.yz.mall.sys.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.yz.mall.sys.service.ExtendSysFilesService;
import io.github.codeyunze.config.QofProperties;
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


    private final QofProperties qofProperties;

    public ExtendSysFilesServiceImpl(QofProperties qofProperties) {
        this.qofProperties = qofProperties;
    }

    @Override
    public List<String> getFilePreviewByFileIds(List<Long> fileIds) {
        String tokenValue = StpUtil.getTokenInfo().getTokenValue();
        List<String> result = new ArrayList<>(fileIds.size());
        fileIds.forEach(fileId -> {
            result.add(qofProperties.getPreviewAddress() + "/" + fileId + "?token=" + tokenValue);
        });
        return result;
    }
}
