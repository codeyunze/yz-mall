package com.yz.mall.sys.extend;

import com.yz.mall.base.IdsDto;
import com.yz.mall.base.Result;
import com.yz.mall.sys.service.ExtendSysFilesService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author yunze
 * @date 2026/1/1 星期四 22:17
 */
@RestController
@RequestMapping("extend/files")
public class ExtendSysFilesController {

    private final ExtendSysFilesService service;

    public ExtendSysFilesController(ExtendSysFilesService service) {
        this.service = service;
    }

    /**
     * 获取文件预览地址
     *
     * @param dto 文件 ID
     * @return 文件预览地址
     */
    @PostMapping("getFilePreviewByFileId")
    Result<List<String>> getFilePreviewByFileId(@RequestBody IdsDto<Long> dto) {
        return Result.success(service.getFilePreviewByFileIds(dto.getIds()));
    }
}
