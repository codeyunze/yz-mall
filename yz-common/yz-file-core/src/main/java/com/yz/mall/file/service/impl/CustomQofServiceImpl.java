package com.yz.mall.file.service.impl;

import com.yz.mall.file.dto.QofFileInfoDto;
import com.yz.mall.file.service.QofService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * QOF文件信息入库操作接口默认实现
 *
 * @author yunze
 * @date 2025/2/18 07:49
 */
@Slf4j
@Service
public class CustomQofServiceImpl implements QofService {

    @Override
    public Long save(QofFileInfoDto dto) {
        log.info("自定义的数据入库实现");
        return dto.getFileId();
    }
}
