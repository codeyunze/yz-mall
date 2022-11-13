package com.yz.common.enums;

/**
 * @ClassName TencentReginEnum
 * @Description 腾讯API的地区编码
 * @Author yunze
 * @Date 2022/11/13 23:23
 * @Version 1.0
 */
public enum TencentReginEnum {

    /**
     * 广州
     */
    GZ("ap-guangzhou"),
    /**
     * 北京
     */
    BJ("ap-beijing"),
    /**
     * 上海
     */
    SH("ap-shanghai"),
    /**
     * 南京
     */
    NJ("ap-nanjing"),
    /**
     * 成都
     */
    CD("ap-chengdu"),
    /**
     * 重庆
     */
    CQ("ap-chongqing");

    private final String value;

    TencentReginEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
