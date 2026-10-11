package com.yz.mall.tw.support;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * JDBC URL 解析单测
 */
class TwClickHouseHttpClientParseTest {

    @Test
    @DisplayName("解析 jdbc:clickhouse URL")
    void parseJdbcUrl() {
        TwClickHouseHttpClient.ParsedJdbc parsed = TwClickHouseHttpClient.parseJdbc("jdbc:clickhouse://127.0.0.1:8123/tw");
        Assertions.assertEquals("http://127.0.0.1:8123", parsed.baseHttpUrl());
        Assertions.assertEquals("tw", parsed.database());
    }
}
