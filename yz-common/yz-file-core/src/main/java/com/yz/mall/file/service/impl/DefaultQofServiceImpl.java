package com.yz.mall.file.service.impl;

import com.yz.mall.file.QofProperties;
import com.yz.mall.file.dto.QofFileInfoDto;
import com.yz.mall.file.service.QofService;
import com.yz.mall.file.service.SysFilesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * QOF文件信息入库操作接口默认实现
 * @author yunze
 * @date 2025/2/18 07:49
 */
@Slf4j
@Service
public class DefaultQofServiceImpl implements QofService {

    @Autowired
    private SysFilesService sysFilesService;

    @Resource
    private QofProperties qofProperties;

    @Override
    public Long save(QofFileInfoDto dto) {
        log.info("默认的数据入库实现");
        if (qofProperties.isPersistentEnable()) {
            return sysFilesService.save(dto);
        }
        return dto.getFileId();
    }
}
