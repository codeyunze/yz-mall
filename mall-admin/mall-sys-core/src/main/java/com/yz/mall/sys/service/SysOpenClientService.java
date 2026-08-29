package com.yz.mall.sys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.base.PageFilter;
import com.yz.mall.sys.dto.*;
import com.yz.mall.sys.entity.SysOpenClient;
import com.yz.mall.sys.entity.SysOpenClientAuth;
import com.yz.mall.sys.entity.SysOpenClientKey;
import com.yz.mall.sys.vo.SysOpenClientCreateVo;
import com.yz.mall.sys.vo.SysOpenClientDetailVo;
import com.yz.mall.sys.vo.SysOpenPermissionOptionVo;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

/**
 * 第三方开放客户端(SysOpenClient)表服务接口
 *
 * @author yunze
 */
public interface SysOpenClientService extends IService<SysOpenClient> {

    /**
     * 新增客户端（自动生成 clientId）
     *
     * @param dto 新增基础数据
     * @return 主键与 clientId
     */
    SysOpenClientCreateVo save(SysOpenClientAddDto dto);

    /**
     * 编辑客户端（不可改 clientId）
     *
     * @param dto 更新基础数据
     * @return 是否操作成功
     */
    boolean update(@Valid SysOpenClientUpdateDto dto);

    /**
     * 切换客户端启停状态
     *
     * @param id 主键Id
     * @return 是否操作成功
     */
    boolean switchStatus(Long id);

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<SysOpenClient> page(PageFilter<SysOpenClientQueryDto> filter);

    /**
     * 详情查询（含当前公钥摘要、授权列表）
     *
     * @param id 主键Id
     * @return 客户端详情
     */
    SysOpenClientDetailVo detail(Long id);

    /**
     * 删除客户端（逻辑删；级联逻辑失效 key/auth）
     *
     * @param id 主键Id
     * @return 是否操作成功
     */
    boolean removeClient(Long id);

    /**
     * 上传客户端公钥（旧 key 停用）
     *
     * @param dto 公钥上传数据
     * @return 是否操作成功
     */
    boolean uploadKey(@Valid SysOpenClientKeyUploadDto dto);

    /**
     * 平台生成 SM2 密钥对：公钥入库，响应中一次性返回私钥
     *
     * @param clientId 客户端标识
     * @return 含公钥与私钥的 Map（私钥仅此次返回）
     */
    Map<String, String> generateKey(String clientId);

    /**
     * 批量授予权限码
     *
     * @param dto 授权操作数据
     * @return 是否操作成功
     */
    boolean grantAuth(@Valid SysOpenClientAuthDto dto);

    /**
     * 撤销指定权限码
     *
     * @param dto 授权操作数据
     * @return 是否操作成功
     */
    boolean revokeAuth(@Valid SysOpenClientAuthDto dto);

    /**
     * 查询客户端有效授权列表
     *
     * @param clientId 客户端标识
     * @return 授权列表
     */
    List<SysOpenClientAuth> listAuth(String clientId);

    /**
     * 查询客户端当前生效公钥
     *
     * @param clientId 客户端标识
     * @return 公钥记录
     */
    SysOpenClientKey getCurrentKey(String clientId);

    /**
     * 系统预置可授权开放 API 清单
     *
     * @return 权限码选项
     */
    List<SysOpenPermissionOptionVo> listPermissionOptions();

    /**
     * 获取平台服务端公钥（供第三方下载）
     *
     * @return 公钥 Base64；未配置时抛业务异常
     */
    String getServerPublicKey();
}
