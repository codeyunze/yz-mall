package com.yz.mall.sys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yz.mall.base.PageFilter;
import com.yz.mall.sys.dto.SysFileQueryDto;
import com.yz.mall.sys.dto.SysFileUpdateDto;
import com.yz.mall.sys.vo.SysFileVo;
import jakarta.validation.Valid;

/**
 * 系统文件元数据管理。
 * <p>
 * 物理文件的上传、下载、删除走 {@code QofClient}；本接口只读写 QOF 元数据。
 *
 * @author yunze
 * @date 2025/12/21 星期日 14:24
 */
public interface SysFileService {

    /**
     * 更新文件元数据（名称、存储模式、存储站）。
     *
     * @param dto 更新数据
     * @return 是否操作成功
     */
    boolean update(@Valid SysFileUpdateDto dto);

    /**
     * 分页查询。
     * <p>
     * 条件对齐 QOF {@code FileMetadataQuery}：文件名模糊、存储模式、存储站。
     * 指定主键时按 ID 精确查询。
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<SysFileVo> page(PageFilter<SysFileQueryDto> filter);

    /**
     * 根据 ID 获取文件信息。
     *
     * @param id 文件 ID
     * @return 文件信息
     */
    SysFileVo getById(Long id);

    /**
     * 删除文件元数据（不删除对象存储中的物理文件）。
     *
     * @param id 文件 ID
     * @return 是否删除成功
     */
    boolean removeById(Long id);
}
