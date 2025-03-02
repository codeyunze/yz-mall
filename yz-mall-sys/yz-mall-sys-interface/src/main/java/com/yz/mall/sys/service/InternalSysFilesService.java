package com.yz.mall.sys.service;

import com.yz.mall.sys.vo.InternalQofFileInfoVo;

import java.util.List;

/**
 * 内部开放接口: 系统管理-文件信息
 *
 * @author yunze
 * @date 2024/6/19 星期三 23:44
 */
public interface InternalSysFilesService {

    /**
     * 根据文件Id列表获取公开的文件信息
     *
     * @param fileIds 文件Id
     * @return 文件信息
     */
    List<InternalQofFileInfoVo> getFileInfoByFileIdsAndPublic(List<Long> fileIds);
}
