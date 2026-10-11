package com.yz.mall.tw.access.constant;

/**
 * 接入常量
 */
public final class TwAccessConstants {

    private TwAccessConstants() {
    }

    public static final String EMQX_ALLOW = "allow";
    public static final String EMQX_DENY = "deny";

    public static final String EVENT_CONNECTED = "client.connected";
    public static final String EVENT_DISCONNECTED = "client.disconnected";
}
