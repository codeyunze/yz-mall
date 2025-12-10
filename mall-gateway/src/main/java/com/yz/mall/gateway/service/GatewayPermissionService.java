package com.yz.mall.gateway.service;

import com.yz.mall.redis.RedisCacheKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 网关权限服务
 * 负责从 Redis 获取接口权限映射（由业务服务启动时扫描并存储）和用户权限，并匹配接口所需权限
 *
 * @author yunze
 * @since 2025-12-05
 */
@Slf4j
@Service
public class GatewayPermissionService {

    @Resource
    private RedisTemplate<String, Object> defaultRedisTemplate;

    /**
     * 路径匹配器
     */
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 权限映射本地缓存（路径 -> 权限标识的映射）
     * Key: 服务名:路径, Value: 权限标识
     * 用于提升性能，减少Redis查询
     */
    private final Map<String, String> permissionMappingCache = new ConcurrentHashMap<>();

    /**
     * 根据请求路径获取所需的权限标识
     * 从Redis中查找接口权限映射（由业务服务启动时扫描Controller并存储）
     * 
     * @param path 请求路径，例如：/sys/dictionary/add
     * @return 权限标识，例如：api:system:dictionary:update，如果不需要权限则返回 null
     */
    public String getRequiredPermission(String path) {
        if (!StringUtils.hasText(path)) {
            return null;
        }

        // 规范化路径
        String normalizedPath = normalizePath(path);
        
        // 先从本地缓存查找
        String cachedPermission = findInCache(normalizedPath);
        if (cachedPermission != null) {
            return cachedPermission;
        }

        // 从Redis查找所有服务的权限映射
        String permission = findInRedis(normalizedPath);
        
        // 如果找到，缓存到本地
        if (permission != null) {
            cachePermission(normalizedPath, permission);
        }

        return permission;
    }

    /**
     * 规范化路径
     */
    private String normalizePath(String path) {
        if (!StringUtils.hasText(path)) {
            return "";
        }
        
        path = path.trim();
        
        // 移除查询参数
        int queryIndex = path.indexOf('?');
        if (queryIndex > 0) {
            path = path.substring(0, queryIndex);
        }
        
        // 确保以斜杠开头
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        
        // 移除末尾的斜杠（除非是根路径）
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        
        return path;
    }

    /**
     * 从本地缓存查找权限
     */
    private String findInCache(String path) {
        // 精确匹配
        for (Map.Entry<String, String> entry : permissionMappingCache.entrySet()) {
            String key = entry.getKey();
            // key格式：服务名:路径 或 all:路径
            String pathPart = key.contains(":") ? key.substring(key.indexOf(':') + 1) : key;
            
            if (pathPart.equals(path)) {
                return entry.getValue();
            }
            
            // 路径匹配（支持通配符）
            if (pathMatcher.match(pathPart, path)) {
                return entry.getValue();
            }
        }
        
        return null;
    }

    /**
     * 从Redis查找权限映射
     * 扫描所有服务的权限映射，查找匹配的路径
     */
    private String findInRedis(String path) {
        try {
            // 查找所有权限映射的key（格式：permission:api-mapping:服务名:路径）
            String pattern = "permission:api-mapping:*";
            Set<String> keys = defaultRedisTemplate.keys(pattern);
            
            if (keys == null || keys.isEmpty()) {
                log.debug("Redis中未找到任何权限映射");
                return null;
            }
            
            // 遍历所有key，查找匹配的路径
            for (String key : keys) {
                // 解析key，提取服务名和路径
                // key格式：permission:api-mapping:服务名:路径
                String[] parts = key.split(":", 4);
                if (parts.length < 4) {
                    continue;
                }
                
                String serviceName = parts[2];
                String mappedPath = "/" + parts[3]; // 恢复路径开头的斜杠
                
                // 精确匹配
                if (mappedPath.equals(path)) {
                    String permission = (String) defaultRedisTemplate.opsForValue().get(key);
                    if (StringUtils.hasText(permission)) {
                        log.debug("从Redis找到权限映射 - 路径: {}, 权限: {}, 服务: {}", path, permission, serviceName);
                        return permission;
                    }
                }
                
                // 路径匹配（支持通配符，如 /sys/dictionary/**）
                if (pathMatcher.match(mappedPath, path)) {
                    String permission = (String) defaultRedisTemplate.opsForValue().get(key);
                    if (StringUtils.hasText(permission)) {
                        log.debug("从Redis找到权限映射（路径匹配） - 路径: {}, 权限: {}, 服务: {}", path, permission, serviceName);
                        return permission;
                    }
                }
            }
            
            log.debug("Redis中未找到路径 [{}] 的权限映射", path);
            return null;
            
        } catch (Exception e) {
            log.error("从Redis查找权限映射失败 - 路径: {}", path, e);
            return null;
        }
    }

    /**
     * 缓存权限映射到本地
     */
    private void cachePermission(String path, String permission) {
        // 使用路径作为key，避免不同服务的路径冲突
        String cacheKey = "all:" + path;
        permissionMappingCache.put(cacheKey, permission);
        
        // 限制缓存大小，避免内存溢出
        if (permissionMappingCache.size() > 10000) {
            // 清理最旧的50%缓存
            int removeCount = permissionMappingCache.size() / 2;
            Iterator<String> iterator = permissionMappingCache.keySet().iterator();
            int count = 0;
            while (iterator.hasNext() && count < removeCount) {
                iterator.next();
                iterator.remove();
                count++;
            }
            log.warn("权限映射缓存已满，清理了 {} 条旧缓存", removeCount);
        }
    }

    /**
     * 获取用户的所有权限列表
     * 
     * @param loginId 用户ID
     * @return 权限列表
     */
    public List<String> getUserPermissions(Object loginId) {
        if (loginId == null) {
            return Collections.emptyList();
        }

        List<String> permissionList = new ArrayList<>();

        // 从 Redis 获取用户角色
        List<Object> roles = defaultRedisTemplate.boundListOps(RedisCacheKey.permissionRole(loginId.toString())).range(0, -1);
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }

        // 获取每个角色的权限
        List<String> roleIds = roles.stream().map(String::valueOf).toList();
        roleIds.forEach(roleId -> {
            List<Object> apiPermissions = defaultRedisTemplate.boundListOps(RedisCacheKey.permission("API", roleId)).range(0, -1);
            if (!CollectionUtils.isEmpty(apiPermissions)) {
                permissionList.addAll(apiPermissions.stream().map(String::valueOf).toList());
            }
        });

        return permissionList.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 刷新权限映射缓存
     * 清空本地缓存，强制从Redis重新加载
     */
    public void refreshPermissionMappingCache() {
        permissionMappingCache.clear();
        log.info("权限映射缓存已刷新");
    }
}
