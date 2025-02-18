package com.yz.mall.file.controller;

import com.yz.mall.file.core.QofClient;
import com.yz.mall.file.dto.QofFileInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
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


    @PutMapping("upload")
    public String upload(@RequestParam("uploadfile") MultipartFile file, @RequestParam("paramValue") String paramvalue, HttpServletRequest request) throws IOException {
        String fileName = file.getOriginalFilename();    // 得到文件名

        QofFileInfoDto fileInfo = new QofFileInfoDto();
        fileInfo.setFileName(fileName);
        fileInfo.setFileLabel(paramvalue);
        fileInfo.setFileType(file.getContentType());
        fileInfo.setFilePath("/test");

        InputStream inputStream = file.getInputStream();
        if ("本地".equals(paramvalue)) {
            return String.valueOf(qofLocalClient.uploadFile(inputStream, fileInfo));
        } else {
            return String.valueOf(qofCosClient.uploadFile(inputStream, fileInfo));
        }
    }
}
