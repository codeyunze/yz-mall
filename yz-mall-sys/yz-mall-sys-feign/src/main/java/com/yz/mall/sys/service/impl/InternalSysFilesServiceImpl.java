package com.yz.mall.sys.service.impl;

import com.yz.mall.sys.feign.InternalSysFilesFeign;
import com.yz.mall.sys.service.InternalSysFilesService;
import com.yz.mall.sys.vo.InternalQofFileInfoVo;
import com.yz.mall.web.common.Result;
import com.yz.mall.web.enums.CodeEnum;
import com.yz.mall.web.exception.FeignException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 内部暴露service实现类: 系统管理-文件信息
 *
 * @author yunze
 * @date 2025/3/2 星期日 20:16
 */
@Service
public class InternalSysFilesServiceImpl implements InternalSysFilesService {

    private final InternalSysFilesFeign internalSysFilesFeign;

    public InternalSysFilesServiceImpl(InternalSysFilesFeign internalSysFilesFeign) {
        this.internalSysFilesFeign = internalSysFilesFeign;
    }

    @Override
    public List<InternalQofFileInfoVo> getFileInfoByFileIdsAndPublic(List<Long> fileIds) {
        Result<List<InternalQofFileInfoVo>> result = internalSysFilesFeign.getFileInfoByFileIdsAndPublic(fileIds);
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new FeignException(result.getCode(), result.getMsg());
        }
        return result.getData();
    }
}
