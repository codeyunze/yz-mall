package com.yz.mall.file.controller;

import com.yz.mall.file.bo.QofFileDownloadBo;
import com.yz.mall.file.core.QofClient;
import com.yz.mall.file.dto.QofFileInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author yunze
 * @date 2024/12/3 00:02
 */
@RestController
@RequestMapping("/file")
public class TestController {

    @RequestMapping("test")
    public String test() {
        return " file test!!!";
    }

    private final QofClient qofLocalClient;
    private final QofClient qofCosClient;

    @Autowired
    public TestController(@Qualifier("qofLocalClient") QofClient qofLocalClient
            , @Qualifier("qofCosClient") QofClient qofCosClient) {
        this.qofLocalClient = qofLocalClient;
        this.qofCosClient = qofCosClient;
    }

    /**
     * 文件上传接口
     *
     * @param file      文件
     * @param fileLabel 标签
     */
    @PutMapping("upload")
    public String upload(@RequestParam("uploadfile") MultipartFile file
            , @RequestParam("fileLabel") String fileLabel) {
        String fileName = file.getOriginalFilename();    // 得到文件名

        QofFileInfoDto fileInfo = new QofFileInfoDto();
        fileInfo.setFileName(fileName);
        fileInfo.setFileLabel(fileLabel);
        fileInfo.setFileType(file.getContentType());
        fileInfo.setDirectoryAddress("/test/202502");
        fileInfo.setFileSize(file.getSize());

        try {
            if ("本地".equals(fileLabel)) {
                return String.valueOf(qofLocalClient.upload(file.getInputStream(), fileInfo));
            } else {
                return String.valueOf(qofCosClient.upload(file.getInputStream(), fileInfo));
            }
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败，异常信息", e);
        }
    }

    /**
     * 文件下载接口
     *
     * @param fileId 文件Id
     */
    @RequestMapping("download/{fileId}")
    public ResponseEntity<StreamingResponseBody> download(@PathVariable Long fileId) {
        QofFileDownloadBo fileDownloadBo = qofCosClient.download(fileId);

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
}
