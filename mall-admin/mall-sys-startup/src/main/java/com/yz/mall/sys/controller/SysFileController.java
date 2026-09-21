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
import io.github.codeyunze.metadata.FileMetadata;
import io.github.codeyunze.metadata.FileMetadataRepository;
import io.github.codeyunze.web.service.FileValidationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.nio.charset.StandardCharsets;

/**
 * 系统-文件管理。
 * <p>
 * 对接 QOF 17.3.2：上传/下载/预览/删除走 {@link QofClient}，鉴权与元数据走 {@link FileMetadataRepository}。
 * 物理删除由客户端生命周期删除元数据，此处不再二次删库。
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
    private final FileMetadataRepository metadataRepository;

    public SysFileController(SysFileService service,
                             QofClientFactory qofClientFactory,
                             FileValidationService fileValidationService,
                             FileMetadataRepository metadataRepository) {
        this.service = service;
        this.qofClientFactory = qofClientFactory;
        this.fileValidationService = fileValidationService;
        this.metadataRepository = metadataRepository;
    }

    /**
     * 文件上传。
     */
    // @SaCheckPermission("api:system:file:edit")
    @PostMapping("/upload")
    public Result<Long> upload(@RequestParam("uploadfile") MultipartFile file,
                               @Valid QofFileUploadDto fileUploadDto) {
        fileUploadDto.setCreateId(StpUtil.getLoginIdAsLong());
        QofFileInfoDto<?> fileInfoDto = fileValidationService.buildFileInfoDto(file, fileUploadDto);
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
     * 文件下载。
     */
    @GetMapping("/download/{fileId}")
    public ResponseEntity<StreamingResponseBody> download(@PathVariable Long fileId) {
        try {
            FileMetadata metadata = requireAccessible(fileId);
            QofFileDownloadBo fileDownloadBo = qofClientFactory.buildClient(metadata.getFileStorageMode()).download(fileId);
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
     * 文件预览。
     */
    @GetMapping("/preview/{fileId}")
    public ResponseEntity<StreamingResponseBody> preview(@PathVariable Long fileId) {
        try {
            FileMetadata metadata = requireAccessible(fileId);
            QofFileDownloadBo fileDownloadBo = qofClientFactory.buildClient(metadata.getFileStorageMode()).preview(fileId);
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
     * 删除物理文件。元数据由 QOF 删除生命周期一并删除。
     */
    // @SaCheckPermission("api:system:file:edit")
    @DeleteMapping("/delete/{fileId}")
    public Result<Boolean> delete(@PathVariable Long fileId) {
        try {
            FileMetadata metadata = requireAccessible(fileId);
            boolean deleted = qofClientFactory.buildClient(metadata.getFileStorageMode()).delete(fileId);
            if (deleted) {
                return new Result<>(HttpStatus.OK.value(), true, "文件删除成功!");
            }
            return new Result<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), false, "文件删除失败");
        } catch (FileAccessDeniedException e) {
            log.warn("文件删除权限被拒绝，文件Id: {}, 原因: {}", fileId, e.getMessage());
            return new Result<>(HttpStatus.FORBIDDEN.value(), false, "文件删除权限被拒绝");
        } catch (Exception e) {
            log.error("文件删除失败，文件Id: {}", fileId, e);
            return new Result<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), false, "文件删除失败: " + e.getMessage());
        }
    }

    /**
     * 更新文件信息。
     */
    // @SaCheckPermission("api:system:file:edit")
    @PostMapping("/update")
    public Result<Boolean> update(@RequestBody @Valid SysFileUpdateDto dto) {
        boolean updated = service.update(dto);
        return updated ? success(true) : Result.error(false, "文件信息更新失败");
    }

    /**
     * 分页查询。
     */
    // @SaCheckPermission("api:system:file:list")
    @PostMapping("/page")
    public Result<ResultTable<SysFileVo>> page(@RequestBody @Valid PageFilter<SysFileQueryDto> filter) {
        var page = service.page(filter);
        return success(page.getRecords(), page.getTotal());
    }

    /**
     * 详情查询。
     */
    @GetMapping("/get/{id}")
    public Result<SysFileVo> get(@PathVariable Long id) {
        return success(service.getById(id));
    }

    /**
     * 校验当前登录用户是否可访问该文件。公开文件直接放行。
     *
     * @param fileId 文件 ID
     * @return 文件元数据（含存储模式）
     */
    private FileMetadata requireAccessible(Long fileId) {
        FileMetadata metadata = metadataRepository.findById(fileId)
                .orElseThrow(() -> new FileAccessDeniedException("文件访问被拒绝：文件不存在"));
        if (metadata.getPublicAccess() != null && metadata.getPublicAccess() == 1) {
            return metadata;
        }
        Long loginId = StpUtil.getLoginIdAsLong();
        if (metadata.getCreateId() == null || !metadata.getCreateId().equals(loginId)) {
            throw new FileAccessDeniedException("文件访问被拒绝：创建者ID不匹配");
        }
        return metadata;
    }
}
