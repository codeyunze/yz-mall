package com.yz.basefile.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文件下载
 * @author yunze
 * @version 1.0
 * @date 2023/4/4 12:37
 */
@RestController
@RequestMapping("/download")
public class FileDownloadController {

    /**
     * 文件下载服务
     * @return
     */
    @RequestMapping
    public byte[] download(){
        return null;
    }

}
