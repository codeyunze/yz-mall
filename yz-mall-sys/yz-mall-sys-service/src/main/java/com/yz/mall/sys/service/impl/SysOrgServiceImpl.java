package com.yz.mall.sys.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.sys.SysProperties;
import com.yz.mall.sys.dto.SysOrgAddDto;
import com.yz.mall.sys.dto.SysOrgQueryDto;
import com.yz.mall.sys.dto.SysOrgUpdateDto;
import com.yz.mall.sys.mapper.SysOrgMapper;
import com.yz.mall.sys.entity.SysOrg;
import com.yz.mall.sys.service.SysOrgService;
import com.yz.mall.web.common.PageFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 系统-组织表(SysOrg)表服务实现类
 *
 * @author yunze
 * @since 2024-11-17 20:19:07
 */
@Service
public class SysOrgServiceImpl extends ServiceImpl<SysOrgMapper, SysOrg> implements SysOrgService {

    @Resource
    private SysProperties sysProperties;

    @Override
    public Long save(SysOrgAddDto dto) {
        // TODO: 2024/11/23 星期六 yunze 添加组织如果parentId是-1，则需校验账号是否是
        SysOrg bo = new SysOrg();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        if (null == bo.getParentId() || -1 == bo.getParentId()) {
            bo.setOrgPathId(String.valueOf(bo.getId()));
        } else {
            SysOrg parentOrg = baseMapper.selectById(bo.getParentId());
            bo.setOrgPathId(parentOrg.getOrgPathId() + "/" + bo.getId());
        }
        baseMapper.insert(bo);
        return bo.getId();
    }

    @Override
    public boolean update(SysOrgUpdateDto dto) {
        SysOrg bo = new SysOrg();
        BeanUtils.copyProperties(dto, bo);
        return baseMapper.updateById(bo) > 0;
    }

    @Override
    public Page<SysOrg> page(PageFilter<SysOrgQueryDto> filter) {
        LambdaQueryWrapper<SysOrg> queryWrapper = new LambdaQueryWrapper<>();
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
    }

    @Override
    public List<SysOrg> list(SysOrgQueryDto filter) {
        LambdaQueryWrapper<SysOrg> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(null != filter.getUserId(), SysOrg::getUserId, filter.getUserId());
        return baseMapper.selectList(queryWrapper);
    }

}

