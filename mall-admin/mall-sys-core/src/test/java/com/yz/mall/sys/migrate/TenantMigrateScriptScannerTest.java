package com.yz.mall.sys.migrate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 租户迁移脚本扫描与配对校验单测
 *
 * @author yunze
 * @since 2026-08-25
 */
class TenantMigrateScriptScannerTest {

    @Test
    @DisplayName("回滚文件名推导")
    void toRollbackFilename() {
        assertEquals("V20260825_001__demo.rollback.sql",
                TenantMigrateScriptScanner.toRollbackFilename("V20260825_001__demo.sql"));
    }

    @Test
    @DisplayName("成对脚本扫描成功")
    void scanAll_pairedScripts() throws Exception {
        Map<String, List<TenantMigrateScriptPair>> result =
                TenantMigrateScriptScanner.scanAll("classpath*:sql/tenant-migrate-test/*/*");
        assertTrue(result.containsKey("demo"));
        assertTrue(result.containsKey(TenantMigrateScriptScanner.COMMON_DIR));
        List<TenantMigrateScriptPair> pairs = result.get("demo");
        assertEquals(1, pairs.size());
        assertEquals("V20260825_001__demo.sql", pairs.get(0).scriptName());
        assertEquals("demo", pairs.get(0).serviceCode());
        assertNotNull(pairs.get(0).checksum());
        assertFalse(pairs.get(0).checksum().isBlank());
    }

    @Test
    @DisplayName("单数据源适用脚本：common 在前再叠加 service")
    void scanApplicableScripts_commonThenService() throws Exception {
        // 先用专用 pattern 扫到 map 的行为由 scanAll 覆盖；此处校验工具方法语义
        assertTrue(TenantMigrateScriptScanner.isCommonDir("common"));
        assertFalse(TenantMigrateScriptScanner.isCommonDir("mall-pms"));
        assertEquals("common/V20260825_001__demo.sql",
                TenantMigrateScriptScanner.toLogScriptName(
                        new TenantMigrateScriptPair("common", "V20260825_001__demo.sql", null, null, "x")));
        assertEquals("V20260825_001__demo.sql",
                TenantMigrateScriptScanner.toLogScriptName(
                        new TenantMigrateScriptPair("mall-pms", "V20260825_001__demo.sql", null, null, "x")));
    }

    @Test
    @DisplayName("缺少回滚脚本时抛出异常")
    void scanAll_missingRollback() {
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> TenantMigrateScriptScanner.scanAll("classpath*:sql/tenant-migrate-broken/*/*"));
        assertTrue(ex.getMessage().contains("缺少配对回滚脚本"));
        assertTrue(ex.getMessage().contains("V20260825_001__broken.sql"));
    }
}
