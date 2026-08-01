package com.yz.mall.sys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.base.PageFilter;
import com.yz.mall.sys.dto.SysFileQueryDto;
import com.yz.mall.sys.dto.SysFileUpdateDto;
import com.yz.mall.sys.vo.SysFileVo;
import io.github.codeyunze.entity.SysFiles;
import jakarta.validation.Valid;

/**
 * 系统文件表(SysFiles)表服务接口
 * <p>
 * 注意：物理文件的上传、下载等操作请使用qof-core和qof-web提供的工具
 * 本Service仅提供文件数据的管理操作
 *
 * @author yunze
 * @date 2025/12/21 星期日 14:24
 */
public interface SysFileService extends IService<SysFiles> {

    /**
     * 更新文件信息
     *
     * @param dto 更新数据
     * @return 是否操作成功
     */
    boolean update(@Valid SysFileUpdateDto dto);

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<SysFileVo> page(PageFilter<SysFileQueryDto> filter);

    /**
     * 根据ID获取文件信息
     *
     * @param id 文件ID
     * @return 文件信息
     */
    SysFileVo getById(Long id);

    /**
     * 删除文件记录（仅删除数据库记录，物理文件删除请使用qof工具）
     *
     * @param id 文件ID
     * @return 是否删除成功
     */
    boolean removeById(Long id);
}
