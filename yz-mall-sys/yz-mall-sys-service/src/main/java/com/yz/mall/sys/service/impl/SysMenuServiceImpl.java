package com.yz.mall.sys.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.sys.dto.SysMenuAddDto;
import com.yz.mall.sys.dto.SysMenuQueryDto;
import com.yz.mall.sys.dto.SysMenuUpdateDto;
import com.yz.mall.sys.entity.SysMenu;
import com.yz.mall.sys.mapper.SysMenuMapper;
import com.yz.mall.sys.service.SysMenuService;
import com.yz.mall.sys.vo.SysMenuSlimVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统-菜单资源表(SysMenu)表服务实现类
 *
 * @author yunze
 * @since 2024-11-21 23:29:02
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {


    @Override
    public Long save(SysMenuAddDto dto) {
        SysMenu bo = new SysMenu();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        bo.setMenuType(dto.getMenuType().get());
        baseMapper.insert(bo);
        return bo.getId();
    }

    @Override
    public boolean update(SysMenuUpdateDto dto) {
        SysMenu bo = new SysMenu();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
    }

    @Override
    public List<SysMenu> list(SysMenuQueryDto filter) {
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<SysMenuSlimVo> listSlim() {
        return baseMapper.selectMenusSlim();
    }
}

