package com.yz.mall.sys.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.yz.mall.base.ApiController;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.Result;
import com.yz.mall.base.ResultTable;
import com.yz.mall.sys.dto.SysFileQueryDto;
import com.yz.mall.sys.dto.SysFileUpdateDto;
import com.yz.mall.sys.service.SysFileService;
import com.yz.mall.sys.vo.SysFileVo;
import io.github.codeyunze.bo.QofFileDownloadBo;
import io.github.codeyunze.core.QofClient;
import io.github.codeyunze.core.QofClientFactory;
import io.github.codeyunze.dto.QofFileInfoDto;
import io.github.codeyunze.dto.QofFileUploadDto;
import io.github.codeyunze.exception.FileAccessDeniedException;
import io.github.codeyunze.service.FileValidationService;
import io.github.codeyunze.service.FilesService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.nio.charset.StandardCharsets;

/**
 * 系统-文件管理
 * <p>
 * 本Controller作为qof-web的FileController的代理层，提供统一的文件管理接口
 * 实际的文件操作通过qof-web提供的工具类实现
 *
 * @author yunze
 * @date 2025/12/21 星期日 0:01
 */
@Slf4j
@RestController
@RequestMapping("/sys/file")
public class SysFileController extends ApiController {

    private final SysFileService service;
    private final QofClientFactory qofClientFactory;
    private final FileValidationService fileValidationService;
    private final FilesService filesService;

    public SysFileController(SysFileService service,
                             QofClientFactory qofClientFactory,
                             FileValidationService fileValidationService,
                             FilesService filesService) {
        this.service = service;
        this.qofClientFactory = qofClientFactory;
        this.fileValidationService = fileValidationService;
        this.filesService = filesService;
    }

    /**
     * 文件上传接口
     * 转发到qof-web的FileController.upload方法
     */
    @SaCheckPermission("api:system:file:edit")
    @PostMapping("/upload")
    public Result<Long> upload(@RequestParam("uploadfile") MultipartFile file,
                               @Valid QofFileUploadDto fileUploadDto) {
        fileUploadDto.setCreateId(StpUtil.getLoginIdAsLong());

        // 构建文件信息DTO（适配层，只做数据转换，不做校验）
        QofFileInfoDto<?> fileInfoDto = fileValidationService.buildFileInfoDto(file, fileUploadDto);

        // 执行文件上传（所有校验都在 AbstractQofClient.upload 中统一处理）
        try {
            QofClient client = qofClientFactory.buildClient(fileUploadDto.getFileStorageMode());
            Long fileId = client.upload(file.getInputStream(), fileInfoDto);
            return new Result<>(HttpStatus.OK.value(), fileId, "文件上传成功");
        } catch (Exception e) {
            log.error("文件上传失败，文件名: {}", fileInfoDto.getFileName(), e);
            return new Result<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, "文件上传失败，请稍后重试");
        }
    }

    /**
     * 文件下载接口
     * 转发到qof-web的FileController.download方法
     */
    @GetMapping("/download/{fileId}")
    public ResponseEntity<StreamingResponseBody> download(@PathVariable Long fileId) {
        try {
            // 先获取文件信息，以确定存储模式
            SysFileVo fileVo = service.getById(fileId);
            String fileStorageMode = fileVo.getFileStorageMode();

            // 校验文件访问权限
            filesService.checkFileAccessPermission(fileId, StpUtil.getLoginIdAsLong());

            QofFileDownloadBo fileDownloadBo = qofClientFactory.buildClient(fileStorageMode).download(fileId);
            StreamingResponseBody streamingResponseBody = fileValidationService.createStreamingResponseBody(
                    fileDownloadBo.getInputStream(), fileId, "下载");
            String encodedFileName = fileValidationService.encodeFileName(fileDownloadBo.getFileName());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + encodedFileName + "\";filename*=UTF-8''" + encodedFileName)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(fileDownloadBo.getFileSize())
                    .body(streamingResponseBody);
        } catch (FileAccessDeniedException e) {
            log.warn("文件下载权限被拒绝，文件Id: {}, 原因: {}", fileId, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            log.error("文件下载失败，文件Id: {}", fileId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 文件预览接口
     * 转发到qof-web的FileController.preview方法
     */
    @GetMapping("/preview/{fileId}")
    public ResponseEntity<StreamingResponseBody> preview(@PathVariable Long fileId) {
        try {
            // 先获取文件信息，以确定存储模式
            SysFileVo fileVo = service.getById(fileId);
            String fileStorageMode = fileVo.getFileStorageMode();

            // 校验文件访问权限
            filesService.checkFileAccessPermission(fileId, StpUtil.getLoginIdAsLong());

            QofFileDownloadBo fileDownloadBo = qofClientFactory.buildClient(fileStorageMode).preview(fileId);

            StreamingResponseBody streamingResponseBody = fileValidationService.createStreamingResponseBody(
                    fileDownloadBo.getInputStream(), fileId, "预览");

            String encodedFileName = fileValidationService.encodeFileName(fileDownloadBo.getFileName());

            ContentDisposition contentDisposition = ContentDisposition.builder("inline")
                    .filename(encodedFileName, StandardCharsets.UTF_8)
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                    .contentType(MediaType.parseMediaType(fileDownloadBo.getFileType()))
                    .body(streamingResponseBody);
        } catch (FileAccessDeniedException e) {
            log.warn("文件预览权限被拒绝，文件Id: {}, 原因: {}", fileId, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            log.error("文件预览失败，文件Id: {}", fileId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 删除文件（删除物理文件和数据库记录）
     * 转发到qof-web的FileController.delete方法
     */
    @SaCheckPermission("api:system:file:edit")
    @DeleteMapping("/delete/{fileId}")
    public Result<Boolean> delete(@PathVariable Long fileId) {
        try {
            // 先获取文件信息，以确定存储模式
            SysFileVo fileVo = service.getById(fileId);
            String fileStorageMode = fileVo.getFileStorageMode();

            // 校验文件访问权限
            filesService.checkFileAccessPermission(fileId, StpUtil.getLoginIdAsLong());

            // 删除物理文件
            boolean deleted = qofClientFactory.buildClient(fileStorageMode).delete(fileId);

            if (deleted) {
                // 删除物理文件成功后，删除数据库记录
                service.removeById(fileId);
                return new Result<>(HttpStatus.OK.value(), true, "文件删除成功!");
            } else {
                return new Result<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), false, "文件删除失败");
            }
        } catch (FileAccessDeniedException e) {
            log.warn("文件删除权限被拒绝，文件Id: {}, 原因: {}", fileId, e.getMessage());
            return new Result<>(HttpStatus.FORBIDDEN.value(), false, "文件删除权限被拒绝");
        } catch (Exception e) {
            log.error("文件删除失败，文件Id: {}", fileId, e);
            return new Result<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), false, "文件删除失败: " + e.getMessage());
        }
    }

    /**
     * 更新文件信息
     */
    @SaCheckPermission("api:system:file:edit")
    @PostMapping("/update")
    public Result<Boolean> update(@RequestBody @Valid SysFileUpdateDto dto) {
        boolean updated = service.update(dto);
        return updated ? success(true) : Result.error(false, "文件信息更新失败");
    }

    /**
     * 分页查询
     */
    @SaCheckPermission("api:system:file:list")
    @PostMapping("/page")
    public Result<ResultTable<SysFileVo>> page(@RequestBody @Valid PageFilter<SysFileQueryDto> filter) {
        var page = service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情查询
     */
    @GetMapping("/get/{id}")
    public Result<SysFileVo> get(@PathVariable Long id) {
        return success(service.getById(id));
    }
}
