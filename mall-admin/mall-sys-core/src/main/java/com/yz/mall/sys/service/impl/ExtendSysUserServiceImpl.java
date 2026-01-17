package com.yz.mall.sys.service.impl;

import com.yz.mall.base.IdsDto;
import com.yz.mall.base.enums.EnableEnum;
import com.yz.mall.base.enums.SexEnum;
import com.yz.mall.sys.dto.ExtendSysUserAddDto;
import com.yz.mall.sys.dto.SysUserAddDto;
import com.yz.mall.sys.entity.SysUser;
import com.yz.mall.sys.service.ExtendSysUserService;
import com.yz.mall.sys.service.SysUserService;
import com.yz.mall.sys.vo.ExtendLoginInfoVo;
import com.yz.mall.sys.vo.ExtendSysUserSlimVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 内部暴露service实现类: 用户信息
 *
 * @author yunze
 * @date 2024/6/19 星期三 23:49
 */
@Service
public class ExtendSysUserServiceImpl implements ExtendSysUserService {

    private final SysUserService service;

    public ExtendSysUserServiceImpl(SysUserService service) {
        this.service = service;
    }

    @Override
    public void deduct(Long userId, BigDecimal amount) {
        this.service.deduct(userId, amount);
    }

    @Override
    public void recharge(Long userId, BigDecimal amount) {
        this.service.recharge(userId, amount);
    }

    @Override
    public List<Long> getUserRoles(Long userId) {
        return service.getUserRoles(userId);
    }

    @Override
    public ExtendLoginInfoVo getUserInfoById(Long userId) {
        return service.getUserInfoById(userId);
    }

    @Override
    public Long add(ExtendSysUserAddDto dto) {
        SysUserAddDto userAddDto = new SysUserAddDto();
        BeanUtils.copyProperties(dto, userAddDto);
        userAddDto.setSex(SexEnum.MALE.get());
        userAddDto.setStatus(EnableEnum.ENABLE.get());
        return service.save(userAddDto);
    }

    @Override
    public Map<Long, ExtendSysUserSlimVo> getUserSlimByIds(IdsDto<Long> idsDto) {
        if (idsDto == null || CollectionUtils.isEmpty(idsDto.getIds())) {
            return Collections.emptyMap();
        }
        
        // 批量查询用户信息
        List<SysUser> users = service.listByIds(idsDto.getIds());
        if (CollectionUtils.isEmpty(users)) {
            return Collections.emptyMap();
        }
        
        // 转换为 VO 并构建 Map
        return users.stream()
                .collect(Collectors.toMap(
                        SysUser::getId,
                        user -> {
                            ExtendSysUserSlimVo vo = new ExtendSysUserSlimVo();
                            vo.setUserId(user.getId());
                            vo.setPhone(user.getPhone());
                            vo.setEmail(user.getEmail());
                            vo.setUsername(user.getUsername());
                            return vo;
                        }
                ));
    }
}
