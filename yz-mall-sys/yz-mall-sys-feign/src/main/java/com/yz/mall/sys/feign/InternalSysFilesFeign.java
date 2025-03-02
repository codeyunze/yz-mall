package com.yz.mall.sys.feign;

import com.yz.mall.sys.vo.InternalQofFileInfoVo;
import com.yz.mall.web.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 内部暴露接口: 文件信息
 *
 * @author yunze
 * @date 2024/6/22 23:44
 */
@FeignClient(name = "yz-mall-sys", contextId = "internalFile", path = "internal/file")
public interface InternalSysFilesFeign {

    /**
     * 根据文件Id列表获取公开的文件信息
     *
     * @param fileIds 文件Id
     * @return 文件信息
     */
    @PostMapping("getFileInfoByFileIdsAndPublic")
    public Result<List<InternalQofFileInfoVo>> getFileInfoByFileIdsAndPublic(@RequestBody List<Long> fileIds);

}
