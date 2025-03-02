package com.yz.mall.file.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.yz.mall.file.dto.YzFileInterviewDto;
import com.yz.mall.file.enums.YzPublicAccessEnum;
import com.yz.mall.file.service.YzSysFilesService;
import com.yz.mall.web.exception.AuthenticationException;
import io.github.codeyunze.QofProperties;
import io.github.codeyunze.bo.QofFileInfoBo;
import io.github.codeyunze.dto.QofFileInfoDto;
import io.github.codeyunze.service.SysFilesService;
import io.github.codeyunze.service.impl.AbstractQofServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author yunze
 * @since 2025/2/25 17:12
 */
@Slf4j
@Primary
@Service
public class YzQofServiceImpl extends AbstractQofServiceImpl {

    private final YzSysFilesService yzSysFilesService;

    public YzQofServiceImpl(SysFilesService filesService, YzSysFilesService yzSysFilesService) {
        super(filesService);
        this.yzSysFilesService = yzSysFilesService;
    }

    @Resource
    private QofProperties qofProperties;

    @Override
    public QofFileInfoBo<?> afterUpload(QofFileInfoDto<?> fileDto) {
        log.info("扩展-自定义-文件上传后执行");
        if (qofProperties.isPersistentEnable()) {
            QofFileInfoDto<YzFileInterviewDto> qofFileInfoDto = new QofFileInfoDto<>();
            BeanUtils.copyProperties(fileDto, qofFileInfoDto);
            return yzSysFilesService.save(qofFileInfoDto);
        }
        QofFileInfoBo<?> fileBo = new QofFileInfoBo<>();
        BeanUtils.copyProperties(fileDto, fileBo);
        return fileBo;
    }

    @Override
    public QofFileInfoBo<?> getFileInfoByFileId(Long fileId) {
        log.info("扩展-自定义-文件信息查询");
        return yzSysFilesService.getFileInfoByFileId(fileId);
    }

    @Override
    public void beforeDownload(QofFileInfoBo<?> fileBo) {
        log.info("扩展-自定义-文件下载前执行");
        // 检查是否可以访问文件
        check(fileBo);
        super.beforeDownload(fileBo);
    }

    @Override
    public boolean beforeDelete(QofFileInfoBo<?> fileBo) {
        log.info("扩展-自定义-文件删除前执行");
        // 检查是否可以访问文件
        check(fileBo);
        return super.beforeDelete(fileBo);
    }

    /**
     * 检查是否可以访问文件
     *
     * @param fileBo 文件信息
     */
    private void check(QofFileInfoBo<?> fileBo) {
        QofFileInfoBo<YzFileInterviewDto> qofFileInfoBo = new QofFileInfoBo<>();
        BeanUtils.copyProperties(fileBo, qofFileInfoBo);
        YzFileInterviewDto interviewDto = qofFileInfoBo.getExtendObject();
        if (YzPublicAccessEnum.NO_ACCESS.getValue() == interviewDto.getPublicAccess()
                && !interviewDto.getCreateId().equals(StpUtil.getLoginIdAsLong())) {
            // 文件未公开访问，且访问则不是文件的归属人
            throw new AuthenticationException("文件不可访问");
        }
    }
}
