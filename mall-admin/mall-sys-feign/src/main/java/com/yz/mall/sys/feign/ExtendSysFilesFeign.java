package com.yz.mall.sys.feign;

import com.yz.mall.base.IdsDto;
import com.yz.mall.base.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 内部暴露接口: 系统管理-文件接口
 * @author yunze
 * @date 2026/1/1 星期四 22:31
 */
@FeignClient(name = "mall-sys", contextId = "extendFiles", path = "extend/files")
public interface ExtendSysFilesFeign {

    /**
     * 获取文件预览地址
     *
     * @param dto 文件 ID
     * @return 文件预览地址
     */
    @PostMapping("getFilePreviewByFileId")
    Result<List<String>> getFilePreviewByFileId(@RequestBody IdsDto<Long> dto);
}
