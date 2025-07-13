package com.yz.mall.base.enums;

/**
 * 菜单类型
 *
 * @author yunze
 * @date 2024/11/22 16:08
 */
public enum MenuTypeEnum {
    /**
     * 0-菜单
     */
    MENU(0),
    /**
     * 1-iframe
     */
    IFRAME(1),
    /**
     * 2-外链接
     */
    LINK(2),
    /**
     * 3-按钮
     */
    BUTTON(3),
    /**
     * 4-接口资源
     */
    API(4)
    ;


    private final Integer value;

    MenuTypeEnum(Integer value) {
        this.value = value;
    }

    public Integer get() {
        return this.value;
    }

}
