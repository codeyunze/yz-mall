package com.yz.mall.sys.service.impl;

import com.yz.mall.base.IdsDto;
import com.yz.mall.base.Result;
import com.yz.mall.base.enums.CodeEnum;
import com.yz.mall.base.exception.FeignException;
import com.yz.mall.sys.feign.ExtendSysFilesFeign;
import com.yz.mall.sys.service.ExtendSysFilesService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 内部暴露service实现类: 系统管理-文件接口
 *
 * @author yunze
 * @date 2026/1/1 星期四 22:34
 */
@Service
public class ExtendSysFilesServiceImpl implements ExtendSysFilesService {

    private final ExtendSysFilesFeign filesFeign;

    public ExtendSysFilesServiceImpl(ExtendSysFilesFeign filesFeign) {
        this.filesFeign = filesFeign;
    }

    @Override
    public List<String> getFilePreviewByFileIds(List<Long> fileIds) {
        IdsDto<Long> dto = new IdsDto<>(fileIds);
        Result<List<String>> result = filesFeign.getFilePreviewByFileId(dto);
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new FeignException(result.getCode(), result.getMsg());
        }
        return result.getData();
    }

}
