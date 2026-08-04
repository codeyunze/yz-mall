package com.yz.mall.tw.device.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.base.PageFilter;
import com.yz.mall.tw.device.dto.TwDeviceAddDto;
import com.yz.mall.tw.device.dto.TwDeviceBindDto;
import com.yz.mall.tw.device.dto.TwDeviceCredResetDto;
import com.yz.mall.tw.device.dto.TwDeviceQueryDto;
import com.yz.mall.tw.device.dto.TwDeviceStatusDto;
import com.yz.mall.tw.device.dto.TwDeviceUnbindDto;
import com.yz.mall.tw.device.dto.TwDeviceUpdateDto;
import com.yz.mall.tw.device.entity.TwDevice;
import com.yz.mall.tw.device.entity.TwDeviceVehicle;
import com.yz.mall.tw.device.vo.TwDeviceCreateVo;
import com.yz.mall.tw.device.vo.TwDeviceCredResetVo;
import com.yz.mall.tw.device.vo.TwDeviceDetailVo;
import com.yz.mall.tw.device.vo.TwDevicePageVo;

/**
 * 终端管理服务
 */
public interface TwDeviceService extends IService<TwDevice> {

    /**
     * 注册终端：生成 deviceId（可指定）与 MQTT 密钥，明文仅本次返回。
     *
     * @param dto 注册入参
     * @return 含一次性明文密码的创建结果
     */
    TwDeviceCreateVo register(TwDeviceAddDto dto);

    /**
     * 编辑终端基础信息（不可改 deviceId）。
     *
     * @param dto 编辑入参
     * @return 是否成功
     */
    Boolean edit(TwDeviceUpdateDto dto);

    /**
     * 启用/禁用终端；禁用时失效鉴权缓存并通知踢连接。
     *
     * @param dto 状态入参
     * @return 是否成功
     */
    Boolean changeStatus(TwDeviceStatusDto dto);

    /**
     * 逻辑删除终端（invalid 置为行 id）；存在有效车辆绑定时拒绝。
     *
     * @param id 终端主键
     * @return 是否成功
     */
    Boolean deleteDevice(Long id);

    /**
     * 分页查询终端（不含密钥）；可按绑定 VIN/在线状态等筛选。
     *
     * @param filter 分页过滤条件
     * @return 分页结果
     */
    Page<TwDevicePageVo> pageDevices(PageFilter<TwDeviceQueryDto> filter);

    /**
     * 终端详情（不含密钥），含当前绑定车辆与在线摘要。
     *
     * @param id 终端主键
     * @return 详情
     */
    TwDeviceDetailVo detail(Long id);

    /**
     * 绑定车辆：校验终端启用、车辆启用，且满足一车一终端、一终端一车。
     *
     * @param dto 绑定入参（id 优先于 deviceId；vehicleId 优先于 vin）
     * @return 绑定记录 ID
     */
    Long bind(TwDeviceBindDto dto);

    /**
     * 解绑当前有效车辆绑定，并失效鉴权缓存。
     *
     * @param dto 解绑定位条件
     * @return 是否成功
     */
    Boolean unbind(TwDeviceUnbindDto dto);

    /**
     * 重置 MQTT 凭证：更新摘要，新明文仅本次返回，并踢旧连接。
     *
     * @param dto 定位终端（id 优先于 deviceId）
     * @return 含新明文密码的重置结果
     */
    TwDeviceCredResetVo resetCredential(TwDeviceCredResetDto dto);

    /**
     * 按主键获取有效终端，不存在则抛业务异常。
     *
     * @param id 终端主键
     * @return 终端实体
     */
    TwDevice requireDevice(Long id);

    /**
     * 按 deviceId 获取有效终端，不存在则抛业务异常。
     *
     * @param deviceId 终端客户端 ID
     * @return 终端实体
     */
    TwDevice requireByDeviceId(String deviceId);

    /**
     * 按终端主键查询当前有效车辆绑定。
     *
     * @param devicePk 终端主键
     * @return 绑定记录，可空
     */
    TwDeviceVehicle getActiveBindByDevicePk(Long devicePk);

    /**
     * 按车辆 ID 查询当前有效终端绑定。
     *
     * @param vehicleId 车辆 ID
     * @return 绑定记录，可空
     */
    TwDeviceVehicle getActiveBindByVehicleId(Long vehicleId);

    /**
     * 按 VIN 查询当前有效终端绑定。
     *
     * @param vin 车架号
     * @return 绑定记录，可空
     */
    TwDeviceVehicle getActiveBindByVin(String vin);
}
