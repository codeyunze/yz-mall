package com.yz.mall.file.core.cos;

import cn.hutool.core.util.IdUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.yz.mall.file.core.QofClient;
import com.yz.mall.file.dto.QofFileInfoDto;
import com.yz.mall.file.service.QofService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 腾讯云文件操作接口实现
 *
 * @author yunze
 * @date 2025/2/17 16:53
 */
@Service
public class QofCosClient implements QofClient {

    @Resource
    private QofCosProperties fileProperties;

    @Resource
    private COSClient cosClient;

    @Autowired
    private List<QofService> qofServices;

    @Override
    public Long uploadFile(InputStream fis, QofFileInfoDto info) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        // 上传的流如果能够获取准确的流长度，则推荐一定填写 content-length
        // 如果确实没办法获取到，则下面这行可以省略，但同时高级接口也没办法使用分块上传了
        // objectMetadata.setContentLength(file.getSize());

        if (info.getFileId() == null) {
            info.setFileId(IdUtil.getSnowflakeNextId());
        }
        String suffix = info.getFileName().substring(info.getFileName().lastIndexOf(".")).toLowerCase();
        String key = fileProperties.getFilepath() + info.getFilePath() + "/" + info.getFileId() + suffix;
        PutObjectRequest putObjectRequest = new PutObjectRequest(fileProperties.getBucketName(), key, fis, objectMetadata);
        // 设置单链接限速（如有需要），不需要可忽略
        putObjectRequest.setTrafficLimit(8 * 1024 * 1024);
        try {
            cosClient.putObject(putObjectRequest);
        } catch (CosServiceException e) {
            e.printStackTrace();
        } catch (CosClientException e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (QofService qofService : qofServices) {
            qofService.save(info);
        }

        return info.getFileId();
    }

}
