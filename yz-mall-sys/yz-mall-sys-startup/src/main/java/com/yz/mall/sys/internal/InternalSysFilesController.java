package com.yz.mall.sys.internal;


import cn.dev33.satoken.stp.StpUtil;
import com.yz.mall.file.service.YzSysFilesService;
import com.yz.mall.sys.dto.InternalSysPendingTasksAddDto;
import com.yz.mall.sys.vo.InternalQofFileInfoVo;
import com.yz.mall.web.common.ApiController;
import com.yz.mall.web.common.Result;
import io.github.codeyunze.bo.QofFileInfoBo;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 内部暴露接口：文件信息
 *
 * @author yunze
 * @since 2024-06-16 23:25:55
 */
@RestController
@RequestMapping("internal/file")
public class InternalSysFilesController extends ApiController {

    private final YzSysFilesService yzSysFilesService;

    public InternalSysFilesController(YzSysFilesService yzSysFilesService) {
        this.yzSysFilesService = yzSysFilesService;
    }

    /**
     * 根据文件Id列表获取公开的文件信息
     *
     * @param fileIds 文件Id
     * @return 文件信息
     */
    @PostMapping("getFileInfoByFileIdsAndPublic")
    public Result<List<InternalQofFileInfoVo>> getFileInfoByFileIdsAndPublic(@RequestBody List<Long> fileIds) {
        List<QofFileInfoBo<String>> infos = yzSysFilesService.getFileInfoByFileIdsAndPublic(fileIds);
        if (CollectionUtils.isEmpty(infos)) {
            return Result.success(Collections.emptyList());
        }

        List<InternalQofFileInfoVo> result = infos.stream().map(t -> {
            InternalQofFileInfoVo vo = new InternalQofFileInfoVo();
            BeanUtils.copyProperties(t, vo);
            vo.setPreviewAddress(t.getExtendObject());
            return vo;
        }).collect(Collectors.toList());
        return Result.success(result);
    }
}

