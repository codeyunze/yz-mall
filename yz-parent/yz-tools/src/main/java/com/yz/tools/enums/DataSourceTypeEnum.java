package com.yz.tools.enums;

/**
 * 多数据源选择项
 *
 * @author yunze
 * @date 2023/10/30 0030 0:11
 */
public enum DataSourceTypeEnum {

    /**
     * 商品数据源
     */
    product("product"),
    /**
     * 库存数据源
     */
    stock("stock");

    private String value;

    DataSourceTypeEnum(String value) {
        this.value = value;
    }

    public String get() {
        return this.value;
    }
}
