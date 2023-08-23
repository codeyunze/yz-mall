package com.yz.ai.face.controller;

import com.yz.ai.face.baiduface.Base64Util;
import com.yz.ai.face.dto.FaceMatchDTO;
import com.yz.ai.face.service.FaceMatch;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yunze
 * @date 2023/7/31 0031 23:20
 */
@Slf4j
@RestController
@RequestMapping("/demo")
public class DemoController {

    @RequestMapping("test")
    public String test() throws IOException {
        File file1 = new File("C:\\Users\\xxx\\Pictures\\Camera Roll\\照片\\9114081c9acd8fa3ee60e7b50728901.jpg");
        File file2 = new File("C:\\Users\\xxx\\Pictures\\Camera Roll\\照片\\微信图片_20230803213829.jpg");

        String img1 = "";
        String img2 = "";

        byte[] bytes1 = new byte[0];
        byte[] bytes2 = new byte[0];

        bytes1 = FileUtils.readFileToByteArray(file1);
        img1 = Base64Util.encode(bytes1);

        bytes2 = FileUtils.readFileToByteArray(file2);
        img2 = Base64Util.encode(bytes2);

        FaceMatchDTO face1 = new FaceMatchDTO();
        face1.setImage(img1);
        face1.setImage_type("BASE64");

        FaceMatchDTO face2 = new FaceMatchDTO();
        face2.setImage(img2);
        face2.setImage_type("BASE64");
        face2.setFace_type("CERT");

        List<FaceMatchDTO> list = new ArrayList<>();
        list.add(face1);
        list.add(face2);

        String s = FaceMatch.faceMatch(list);
        return s;
    }
}
