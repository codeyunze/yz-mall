package com.yz.mall.file.core.local;

import cn.hutool.core.util.IdUtil;
import com.yz.mall.file.core.QofClient;
import com.yz.mall.file.dto.QofFileInfoDto;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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

    @Override
    public Long uploadFile(InputStream fis, QofFileInfoDto info) {
        if (info.getFileId() == null) {
            info.setFileId(IdUtil.getSnowflakeNextId());
        }

        String suffix = info.getFileName().substring(info.getFileName().lastIndexOf(".")).toLowerCase();
        String key = fileProperties.getFilepath() + info.getFilePath();

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

        return info.getFileId();
    }
}
