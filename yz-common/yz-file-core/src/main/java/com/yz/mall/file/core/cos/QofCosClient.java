package com.yz.mall.file.core.cos;

import cn.hutool.core.util.IdUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.utils.IOUtils;
import com.yz.mall.file.bo.QofFileDownloadBo;
import com.yz.mall.file.bo.QofFileInfoBo;
import com.yz.mall.file.core.QofClient;
import com.yz.mall.file.dto.QofFileInfoDto;
import com.yz.mall.file.service.QofService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;

/**
 * 腾讯云文件操作接口实现
 *
 * @author yunze
 * @date 2025/2/17 16:53
 */
@Slf4j
@Service
public class QofCosClient implements QofClient {

    @Resource
    private QofCosProperties fileProperties;

    @Resource
    private COSClient cosClient;

    private final QofService qofService;

    public QofCosClient(QofService qofService) {
        this.qofService = qofService;
    }

    @Override
    public Long upload(InputStream fis, QofFileInfoDto info) {
        if (info.getFileId() == null) {
            info.setFileId(IdUtil.getSnowflakeNextId());
        }
        // 执行文件上传前操作
        qofService.beforeUpload(info);

        String suffix = info.getFileName().substring(info.getFileName().lastIndexOf(".")).toLowerCase();
        String key = info.getDirectoryAddress() + "/" + info.getFileId() + suffix;
        info.setFilePath(key);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        // 上传的流如果能够获取准确的流长度，则推荐一定填写 content-length
        // 如果确实没办法获取到，则下面这行可以省略，但同时高级接口也没办法使用分块上传了
        objectMetadata.setContentLength(info.getFileSize());
        PutObjectRequest putObjectRequest = new PutObjectRequest(fileProperties.getBucketName(), fileProperties.getFilepath() + key, fis, objectMetadata);
        // 设置单链接限速（如有需要），不需要可忽略
        putObjectRequest.setTrafficLimit(8 * 1024 * 1024);
        try {
            cosClient.putObject(putObjectRequest);
        } catch (CosServiceException e) {
            log.error("COS服务异常: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败，服务异常", e);
        } catch (CosClientException e) {
            log.error("COS客户端异常: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败，客户端异常", e);
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 执行文件上传后操作
        qofService.afterUpload(info);
        return info.getFileId();
    }

    @Override
    public QofFileDownloadBo download(Long fileId) {
        QofFileInfoBo fileBo = qofService.beforeDownload(fileId);

        GetObjectRequest getObjectRequest = new GetObjectRequest(fileProperties.getBucketName(), fileProperties.getFilepath() + fileBo.getFilePath());
        COSObject cosObject = cosClient.getObject(getObjectRequest);

        QofFileDownloadBo fileDownloadBo = new QofFileDownloadBo();
        BeanUtils.copyProperties(fileBo, fileDownloadBo);
        fileDownloadBo.setInputStream(cosObject.getObjectContent());

        qofService.afterDownload(fileId);
        return fileDownloadBo;
    }
}
