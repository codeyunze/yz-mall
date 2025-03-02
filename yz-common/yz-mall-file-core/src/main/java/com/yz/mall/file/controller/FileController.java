package com.yz.mall.file.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.yz.mall.file.dto.YzFileInterviewDto;
import com.yz.mall.file.dto.YzFileUploadDto;
import com.yz.mall.web.enums.CodeEnum;
import io.github.codeyunze.bo.QofFileDownloadBo;
import io.github.codeyunze.core.QofClient;
import io.github.codeyunze.core.QofClientFactory;
import io.github.codeyunze.dto.QofFileInfoDto;
import io.github.codeyunze.dto.QofFileUniversalDto;
import io.github.codeyunze.dto.QofFileUploadDto;
import io.github.codeyunze.entity.SysFiles;
import io.github.codeyunze.utils.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * @author yunze
 * @since 2024/12/3 00:02
 */
@RestController
@RequestMapping("/file")
public class FileController {

    private final QofClientFactory qofClientFactory;

    public FileController(QofClientFactory qofClientFactory) {
        this.qofClientFactory = qofClientFactory;
    }

    /**
     * 文件上传接口
     *
     * @param file          文件
     * @param fileUploadDto 文件信息
     * @return 文件Id
     */
    @SaCheckPermission("api:system:file:upload")
    @PutMapping("upload")
    public Result<Long> upload(@RequestParam("uploadfile") MultipartFile file
            , @Valid YzFileUploadDto fileUploadDto) {
        QofFileInfoDto<YzFileInterviewDto> fileInfoDto = new QofFileInfoDto<>();
        BeanUtils.copyProperties(fileUploadDto, fileInfoDto);

        YzFileInterviewDto interviewDto = new YzFileInterviewDto();
        interviewDto.setCreateId(StpUtil.getLoginIdAsLong());
        interviewDto.setPublicAccess(fileUploadDto.getPublicAccess());
        fileInfoDto.setExtendObject(interviewDto);

        if (!StringUtils.hasText(fileUploadDto.getFileName())) {
            // 得到文件名
            String fileName = file.getOriginalFilename();
            fileInfoDto.setFileName(fileName);
        }
        fileInfoDto.setFileType(file.getContentType());
        fileInfoDto.setDirectoryAddress("/" + LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.SIMPLE_MONTH_PATTERN));
        fileInfoDto.setFileSize(file.getSize());

        try {
            QofClient client = qofClientFactory.buildClient(fileUploadDto.getFileStorageMode());
            return new Result<>(CodeEnum.SUCCESS.get(), client.upload(file.getInputStream(), fileInfoDto), "文件上传成功");
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败，异常信息", e);
        }
    }

    /**
     * 文件下载接口
     *
     * @param fileDownloadDto 下载文件信息
     * @return 文件流信息
     */
    @SaCheckPermission("api:system:file:download")
    @GetMapping("download")
    public ResponseEntity<StreamingResponseBody> download(@RequestBody @Valid QofFileUniversalDto fileDownloadDto) {
        QofFileDownloadBo fileDownloadBo = qofClientFactory.buildClient(fileDownloadDto.getFileStorageMode()).download(fileDownloadDto.getFileId());

        StreamingResponseBody streamingResponseBody = outputStream -> {
            try (InputStream inputStream = fileDownloadBo.getInputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileDownloadBo.getFileName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                // 告诉浏览器文件的大小，以显示文件的下载进度
                .contentLength(fileDownloadBo.getFileSize())
                .body(streamingResponseBody);
    }

    /**
     * 预览文件
     *
     * @param fileId          文件唯一Id
     * @param fileStorageMode 文件存储的策略 {@link SysFiles#getFileStorageMode()}
     * @return 文件流信息
     */
    @SaCheckPermission("api:system:file:preview")
    @GetMapping("preview")
    public ResponseEntity<StreamingResponseBody> preview(@RequestParam("fileId") Long fileId, @RequestParam("fileStorageMode") String fileStorageMode) {
        QofFileDownloadBo fileDownloadBo = qofClientFactory.buildClient(fileStorageMode).preview(fileId);

        StreamingResponseBody streamingResponseBody = outputStream -> {
            try (InputStream inputStream = fileDownloadBo.getInputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        };

        ContentDisposition contentDisposition = ContentDisposition.builder("inline")
                .filename(fileDownloadBo.getFileName(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .contentType(MediaType.parseMediaType(fileDownloadBo.getFileType()))
                .body(streamingResponseBody);
    }

    /**
     * 预览文件
     *
     * @param fileId          文件唯一Id
     * @param fileStorageMode 文件存储的策略 {@link SysFiles#getFileStorageMode()}
     * @return 文件流信息
     */
    @SaIgnore
    @GetMapping("/public/preview")
    public ResponseEntity<StreamingResponseBody> publicPreview(@RequestParam("fileId") Long fileId, @RequestParam("fileStorageMode") String fileStorageMode) {
        QofFileDownloadBo fileDownloadBo = qofClientFactory.buildClient(fileStorageMode).preview(fileId);

        StreamingResponseBody streamingResponseBody = outputStream -> {
            try (InputStream inputStream = fileDownloadBo.getInputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        };

        ContentDisposition contentDisposition = ContentDisposition.builder("inline")
                .filename(fileDownloadBo.getFileName(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .contentType(MediaType.parseMediaType(fileDownloadBo.getFileType()))
                .body(streamingResponseBody);
    }

    /**
     * 删除文件
     *
     * @param fileUniversalDto 删除信息
     * @return 是否删除成功   true: 文件删除成功;   false: 文件删除失败;
     */
    @SaCheckPermission("api:system:file:delete")
    @DeleteMapping("delete")
    public Result<Boolean> delete(@RequestBody @Valid QofFileUniversalDto fileUniversalDto) {
        boolean deleted = qofClientFactory.buildClient(fileUniversalDto.getFileStorageMode()).delete(fileUniversalDto.getFileId());
        return new Result<>(HttpStatus.OK.value(), deleted, deleted ? "文件删除成功!" : "文件删除失败");
    }
}
