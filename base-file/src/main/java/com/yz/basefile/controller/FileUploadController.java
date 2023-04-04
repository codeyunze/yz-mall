package com.yz.basefile.controller;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * @author yunze
 * @version 1.0
 * @date 2023/4/4 12:37
 */
@Slf4j
@RestController
@RequestMapping("/upload")
public class FileUploadController {

    @Value("${yz.file.path}")
    private String path;


    @RequestMapping(value = "/{filename}")
    public String upload(MultipartFile multipartFile, @PathVariable String filename) {
        try {
            String date = DateUtil.format(DateUtil.date(), "yyyyMMdd");
            String filePath = path + "/images/" + date + "/";
            File file = new File(filePath, filename);
            if (!file.exists()) {
                file.mkdirs();
            }

            multipartFile.transferTo(file);
        } catch (IOException e) {
            return "fail";
        }
        return "success";
    }

    @RequestMapping(value = "/v2/{filename}")
    public String uploadV2(MultipartFile multipartFile, @PathVariable String filename) {
        try (InputStream is = multipartFile.getInputStream(); ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            IOUtils.copy(is, os);
            byte[] bytes = os.toByteArray();
            String date = DateUtil.format(DateUtil.date(), "yyyyMMdd");
            String filePath = path + "/images/" + date + "/";
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(filePath + filename);
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            IOUtils.copy(bis, fos);
            fos.close();
            bis.close();
        } catch (IOException e) {
            return "fail";
        }
        return "success";
    }
}
