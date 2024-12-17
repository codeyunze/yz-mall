package com.yz.mall.sys.validators;

import com.yz.mall.sys.dto.SysMenuAddDto;
import com.yz.mall.sys.enums.MenuTypeEnum;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


/**
 * 新增菜单信息校验
 *
 * @author yunze
 * @date 2024/11/22 16:15
 */
public class SysMenuAddValidator implements ConstraintValidator<ValidSysMenuAdd, SysMenuAddDto> {

    @Override
    public void initialize(ValidSysMenuAdd constraintAnnotation) {
    }

    @Override
    public boolean isValid(SysMenuAddDto dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return false;
        }

        MenuTypeEnum menuType = dto.getMenuType();

        if (menuType == MenuTypeEnum.MENU
                || menuType == MenuTypeEnum.IFRAME
                || menuType == MenuTypeEnum.LINK) {
            boolean nameValid = addNameValid(dto, context);
            boolean pathValid = addPathValid(dto, context);
            return nameValid && pathValid;
        } else if (menuType == MenuTypeEnum.BUTTON || menuType == MenuTypeEnum.API) {
            return addAuthsValid(dto, context);
        }

        return true;
    }

    /**
     * 添加路由名称属性校验
     */
    private boolean addNameValid(SysMenuAddDto dto, ConstraintValidatorContext context) {
        if (StringUtils.hasText(dto.getName())) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("路由名称不能为空").addPropertyNode("name").addConstraintViolation();
        return false;
    }

    /**
     * 添加路由路径属性校验
     */
    private boolean addPathValid(SysMenuAddDto dto, ConstraintValidatorContext context) {
        if (StringUtils.hasText(dto.getPath())) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("路由路径不能为空").addPropertyNode("path").addConstraintViolation();
        return false;
    }

    /**
     * 添加按钮权限标识属性校验
     */
    private boolean addAuthsValid(SysMenuAddDto dto, ConstraintValidatorContext context) {
        if (StringUtils.hasText(dto.getAuths())) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("权限标识不能为空").addPropertyNode("auths").addConstraintViolation();
        return false;
    }
}
