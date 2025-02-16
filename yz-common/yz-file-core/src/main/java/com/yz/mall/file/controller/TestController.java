package com.yz.mall.file.controller;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.yz.mall.file.core.cos.QofCosProperties;
import com.yz.mall.file.dto.SysFilesAddDto;
import com.yz.mall.file.service.SysFilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
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

    @Autowired
    private SysFilesService service;

    @Resource
    private COSClient cosClient;

    @Resource
    private QofCosProperties fileProperties;

    @RequestMapping("upload")
    public String upload(@RequestParam("uploadfile") MultipartFile file, @RequestParam("paramValue") String paramvalue, HttpServletRequest request) throws IOException {
        String fileName = file.getOriginalFilename();    // 得到文件名

        SysFilesAddDto filesAddDto = new SysFilesAddDto();
        filesAddDto.setFileName(fileName);
        filesAddDto.setFileLabel(paramvalue);
        filesAddDto.setFileType(file.getContentType());
        filesAddDto.setFilePath("/test/" + fileName);
        service.save(filesAddDto);

        InputStream inputStream = file.getInputStream();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        // 上传的流如果能够获取准确的流长度，则推荐一定填写 content-length
        // 如果确实没办法获取到，则下面这行可以省略，但同时高级接口也没办法使用分块上传了
        objectMetadata.setContentLength(file.getSize());

        PutObjectRequest putObjectRequest = new PutObjectRequest(fileProperties.getBucketName(), fileProperties.getFilepath() + filesAddDto.getFilePath(), inputStream, objectMetadata);
        // 设置单链接限速（如有需要），不需要可忽略
        putObjectRequest.setTrafficLimit(8 * 1024 * 1024);
        try {
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
            System.out.println(putObjectResult.getRequestId());
        } catch (CosServiceException e) {
            e.printStackTrace();
        } catch (CosClientException e) {
            e.printStackTrace();
        }
        return "success";
    }
}
