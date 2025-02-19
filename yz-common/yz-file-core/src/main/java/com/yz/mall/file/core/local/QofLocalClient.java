package com.yz.mall.file.core.local;

import cn.hutool.core.util.IdUtil;
import com.yz.mall.file.bo.QofFileDownloadBo;
import com.yz.mall.file.bo.QofFileInfoBo;
import com.yz.mall.file.core.QofClient;
import com.yz.mall.file.dto.QofFileInfoDto;
import com.yz.mall.file.service.QofService;
import com.yz.mall.web.exception.DataNotExistException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * 本地文件操作接口实现
 *
 * @author yunze
 * @date 2025/2/17 16:53
 */
@Service
public class QofLocalClient implements QofClient {

    @Resource
    private QofLocalProperties fileProperties;

    private final QofService qofService;

    public QofLocalClient(QofService qofService) {
        this.qofService = qofService;
    }

    @Override
    public Long upload(InputStream fis, QofFileInfoDto info) {
        if (info.getFileId() == null) {
            info.setFileId(IdUtil.getSnowflakeNextId());
        }
        qofService.beforeUpload(info);

        String suffix = info.getFileName().substring(info.getFileName().lastIndexOf(".")).toLowerCase();
        String key = fileProperties.getFilepath() + info.getDirectoryAddress();

        // 确保上传目录存在
        Path uploadPath = Paths.get(key);
        if (!Files.exists(uploadPath)) {
            try {
                // 创建目录
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        String fileName = info.getFileId() + suffix;
        info.setFilePath(info.getDirectoryAddress() + "/" + fileName);

        // 定义目标文件路径
        Path filePath = uploadPath.resolve(fileName);
        try {
            // 使用NIO将输入流复制到目标文件，如果文件已经存在，则覆盖
            Files.copy(fis, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        qofService.afterUpload(info);
        return info.getFileId();
    }

    @Override
    public QofFileDownloadBo download(Long fileId) {
        QofFileInfoBo fileBo = qofService.getFileInfoByFileId(fileId);
        qofService.beforeDownload(fileId);
        // 确保文件路径正确构建
        String filePath = fileProperties.getFilepath() + fileBo.getFilePath();
        File file = new File(filePath);

        if (!file.exists()) {
            throw new DataNotExistException("文件不存在");
        }

        QofFileDownloadBo fileDownloadBo = new QofFileDownloadBo();
        BeanUtils.copyProperties(fileBo, fileDownloadBo);
        try {
            fileDownloadBo.setInputStream(Files.newInputStream(Paths.get(file.getPath())));
        } catch (IOException e) {
            throw new RuntimeException("下载文件时发生错误", e);
        }

        qofService.afterDownload(fileId);
        return fileDownloadBo;
    }

    @Override
    public boolean delete(Long fileId) {
        QofFileInfoBo fileBo = qofService.getFileInfoByFileId(fileId);
        if (fileBo == null) {
            return true;
        }
        if (!qofService.beforeDelete(fileId)) {
            return false;
        }
        // 确保文件路径正确构建
        String filePath = fileProperties.getFilepath() + fileBo.getFilePath();
        File file = new File(filePath);

        if (!file.exists()) {
            return true;
        }
        return file.delete();
    }
}
