package com.yz.mall.file.controller;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.yz.mall.file.bo.QofFileDownloadBo;
import com.yz.mall.file.core.QofClient;
import com.yz.mall.file.dto.QofFileDeleteDto;
import com.yz.mall.file.dto.QofFileDownloadDto;
import com.yz.mall.file.dto.QofFileInfoDto;
import com.yz.mall.file.dto.QofFileUploadDto;
import com.yz.mall.file.enums.QofStorageModeEnum;
import com.yz.mall.web.common.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

/**
 * @author yunze
 * @date 2024/12/3 00:02
 */
@RestController
@RequestMapping("/file")
public class FileController {

    @RequestMapping("test")
    public String test() {
        return " file test!!!";
    }

    private final QofClient qofLocalClient;
    private final QofClient qofCosClient;

    @Autowired
    public FileController(@Qualifier("qofLocalClient") QofClient qofLocalClient
            , @Qualifier("qofCosClient") QofClient qofCosClient) {
        this.qofLocalClient = qofLocalClient;
        this.qofCosClient = qofCosClient;
    }

    /**
     * 文件上传接口
     *
     * @param file          文件
     * @param fileUploadDto 文件信息
     */
    @PutMapping("upload")
    public String upload(@RequestParam("uploadfile") MultipartFile file
            , @Valid QofFileUploadDto fileUploadDto) {
        QofFileInfoDto fileInfoDto = new QofFileInfoDto();
        BeanUtils.copyProperties(fileUploadDto, fileInfoDto);

        if (!StringUtils.hasText(fileUploadDto.getFileName())) {
            String fileName = file.getOriginalFilename();    // 得到文件名
            fileInfoDto.setFileName(fileName);
        }
        fileInfoDto.setFileType(file.getContentType());
        fileInfoDto.setDirectoryAddress("/default/" + LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.SIMPLE_MONTH_PATTERN));
        fileInfoDto.setFileSize(file.getSize());

        try {
            if (QofStorageModeEnum.COS.getMode().equals(fileUploadDto.getFileStorageMode())) {
                return String.valueOf(qofCosClient.upload(file.getInputStream(), fileInfoDto));
            } else {
                return String.valueOf(qofLocalClient.upload(file.getInputStream(), fileInfoDto));
            }
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败，异常信息", e);
        }
    }

    /**
     * 文件下载接口
     *
     * @param fileDownloadDto 下载文件信息
     */
    @GetMapping("download")
    public ResponseEntity<StreamingResponseBody> download(@RequestBody @Valid QofFileDownloadDto fileDownloadDto) {
        QofFileDownloadBo fileDownloadBo;
        if (QofStorageModeEnum.COS.getMode().equals(fileDownloadDto.getFileStorageMode())) {
            fileDownloadBo = qofCosClient.download(fileDownloadDto.getFileId());
        } else {
            fileDownloadBo = qofLocalClient.download(fileDownloadDto.getFileId());
        }

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


    @RequestMapping("preview")
    public void preview() {

    }

    /**
     * 删除文件
     *
     * @param fileDeleteDto 删除信息
     * @return 是否删除成功   true: 文件删除成功;   false: 文件删除失败;
     */
    @DeleteMapping("delete")
    public Result<Boolean> delete(@RequestBody @Valid QofFileDeleteDto fileDeleteDto) {
        boolean deleted;
        if (QofStorageModeEnum.COS.getMode().equals(fileDeleteDto.getFileStorageMode())) {
            deleted = qofCosClient.delete(fileDeleteDto.getFileId());
        } else {
            deleted = qofLocalClient.delete(fileDeleteDto.getFileId());
        }
        return Result.success(deleted);
    }
}
