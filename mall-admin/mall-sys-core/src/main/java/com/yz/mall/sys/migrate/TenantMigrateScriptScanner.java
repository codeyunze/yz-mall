package com.yz.mall.sys.migrate;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * 扫描 classpath:sql/tenant-migrate/{serviceCode|common}/ 下的成对正/回滚脚本。
 * <p>
 * 目录名为 {@link #COMMON_DIR} 时表示公共脚本，应对所有启用数据源执行。
 *
 * @author yunze
 * @since 2026-08-25
 */
public final class TenantMigrateScriptScanner {

    public static final String BASE_LOCATION = "classpath*:sql/tenant-migrate/";

    /**
     * 公共脚本目录名：其下脚本对所有服务数据源执行
     */
    public static final String COMMON_DIR = "common";

    private static final String ROLLBACK_SUFFIX = ".rollback.sql";
    private static final String SQL_SUFFIX = ".sql";

    private TenantMigrateScriptScanner() {
    }

    /**
     * 是否为公共脚本目录。
     *
     * @param dirName 目录名（扫描得到的 serviceCode）
     * @return true 表示应对全部启用数据源执行
     */
    public static boolean isCommonDir(String dirName) {
        return COMMON_DIR.equals(dirName);
    }

    /**
     * 扫描全部 serviceCode 目录，按 serviceCode、脚本名排序返回成对脚本。
     *
     * @return serviceCode → 有序脚本对列表
     * @throws IOException 资源读取失败
     * @throws IllegalStateException 存在正向脚本缺少配对回滚脚本
     */
    public static Map<String, List<TenantMigrateScriptPair>> scanAll() throws IOException {
        return scanAll(BASE_LOCATION + "*/*");
    }

    /**
     * 按资源匹配模式扫描成对脚本（单测可传入测试 classpath 模式）。
     *
     * @param locationPattern Spring 资源匹配模式（如扫描 tenant-migrate 下各 service 目录脚本）
     * @return serviceCode → 有序脚本对列表
     */
    public static Map<String, List<TenantMigrateScriptPair>> scanAll(String locationPattern) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(locationPattern);
        Map<String, Map<String, Resource>> forwardByService = new TreeMap<>();
        Map<String, Map<String, Resource>> rollbackByService = new TreeMap<>();

        for (Resource resource : resources) {
            if (!resource.isReadable() || resource.getFilename() == null) {
                continue;
            }
            String filename = resource.getFilename();
            String serviceCode = extractServiceCode(resource);
            if (serviceCode == null || serviceCode.isBlank()) {
                continue;
            }
            if (filename.endsWith(ROLLBACK_SUFFIX)) {
                String forwardName = filename.substring(0, filename.length() - ROLLBACK_SUFFIX.length()) + SQL_SUFFIX;
                rollbackByService.computeIfAbsent(serviceCode, k -> new TreeMap<>()).put(forwardName, resource);
            } else if (filename.toLowerCase(Locale.ROOT).endsWith(SQL_SUFFIX) && !filename.endsWith(ROLLBACK_SUFFIX)) {
                forwardByService.computeIfAbsent(serviceCode, k -> new TreeMap<>()).put(filename, resource);
            }
        }

        Map<String, List<TenantMigrateScriptPair>> result = new LinkedHashMap<>();
        for (Map.Entry<String, Map<String, Resource>> entry : forwardByService.entrySet()) {
            String serviceCode = entry.getKey();
            Map<String, Resource> forwards = entry.getValue();
            Map<String, Resource> rollbacks = rollbackByService.getOrDefault(serviceCode, Map.of());
            List<TenantMigrateScriptPair> pairs = new ArrayList<>();
            for (Map.Entry<String, Resource> forwardEntry : forwards.entrySet()) {
                String scriptName = forwardEntry.getKey();
                Resource rollback = rollbacks.get(scriptName);
                if (rollback == null) {
                    throw new IllegalStateException("缺少配对回滚脚本: serviceCode=" + serviceCode + ", script=" + scriptName
                            + ", 期望=" + toRollbackFilename(scriptName));
                }
                String checksum = checksum(forwardEntry.getValue());
                pairs.add(new TenantMigrateScriptPair(serviceCode, scriptName, forwardEntry.getValue(), rollback, checksum));
            }
            pairs.sort(Comparator.comparing(TenantMigrateScriptPair::scriptName));
            result.put(serviceCode, pairs);
        }
        return result;
    }

    /**
     * 扫描指定 serviceCode 目录下的成对脚本（不含 common）。
     *
     * @param serviceCode 服务标识
     * @return 有序脚本对
     */
    public static List<TenantMigrateScriptPair> scanByServiceCode(String serviceCode) throws IOException {
        Map<String, List<TenantMigrateScriptPair>> all = scanAll();
        return all.getOrDefault(serviceCode, List.of());
    }

    /**
     * 单数据源适用脚本：先 common，再该 serviceCode 目录（common 目录本身不再重复叠加）。
     *
     * @param serviceCode 数据源服务标识
     * @return 有序脚本对（common 在前）
     */
    public static List<TenantMigrateScriptPair> scanApplicableScripts(String serviceCode) throws IOException {
        Map<String, List<TenantMigrateScriptPair>> all = scanAll();
        List<TenantMigrateScriptPair> result = new ArrayList<>();
        List<TenantMigrateScriptPair> common = all.get(COMMON_DIR);
        if (common != null && !common.isEmpty()) {
            result.addAll(common);
        }
        if (serviceCode != null && !serviceCode.isBlank() && !isCommonDir(serviceCode)) {
            List<TenantMigrateScriptPair> specific = all.get(serviceCode);
            if (specific != null && !specific.isEmpty()) {
                result.addAll(specific);
            }
        }
        return result;
    }

    /**
     * 日志/幂等用的脚本名：公共脚本加 {@code common/} 前缀，避免与服务目录同名脚本冲突。
     *
     * @param pair 脚本对
     * @return 日志中的 script_name
     */
    public static String toLogScriptName(TenantMigrateScriptPair pair) {
        if (pair == null) {
            return null;
        }
        if (isCommonDir(pair.serviceCode())) {
            return COMMON_DIR + "/" + pair.scriptName();
        }
        return pair.scriptName();
    }

    /**
     * 由正向文件名推导回滚文件名。
     *
     * @param forwardFilename 正向脚本名，如 V20260825_001__x.sql
     * @return 回滚脚本名，如 V20260825_001__x.rollback.sql
     */
    public static String toRollbackFilename(String forwardFilename) {
        if (forwardFilename.endsWith(SQL_SUFFIX)) {
            return forwardFilename.substring(0, forwardFilename.length() - SQL_SUFFIX.length()) + ROLLBACK_SUFFIX;
        }
        return forwardFilename + ROLLBACK_SUFFIX;
    }

    /**
     * 从 Resource URL 解析 serviceCode（脚本文件上一级目录名）。
     */
    static String extractServiceCode(Resource resource) throws IOException {
        String filename = resource.getFilename();
        if (filename == null || filename.isBlank()) {
            return null;
        }
        String url = resource.getURL().toString().replace('\\', '/');
        int fileIdx = url.lastIndexOf('/' + filename);
        if (fileIdx <= 0) {
            return null;
        }
        String withoutFile = url.substring(0, fileIdx);
        int slash = withoutFile.lastIndexOf('/');
        if (slash < 0 || slash >= withoutFile.length() - 1) {
            return null;
        }
        return withoutFile.substring(slash + 1);
    }

    /**
     * 计算资源内容 SHA-256 十六进制摘要。
     */
    public static String checksum(Resource resource) throws IOException {
        byte[] bytes = StreamUtils.copyToByteArray(resource.getInputStream());
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(bytes));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 不可用", e);
        }
    }

    /**
     * 读取脚本文本（测试或调试用）。
     */
    public static String readUtf8(Resource resource) throws IOException {
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }
}
