package com.yz.mall.auth.scanner;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.yz.mall.redis.RedisCacheKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 权限映射扫描器
 * 在应用启动时扫描所有Controller接口，解析@SaCheckPermission注解
 * 将接口URI和权限标识的映射关系存储到Redis
 *
 * @author yunze
 * @since 2025-12-05
 */
@Slf4j
@Component
public class PermissionMappingScanner implements ApplicationListener<ApplicationReadyEvent> {

    @Resource
    private RedisTemplate<String, Object> defaultRedisTemplate;

    @Resource
    private ApplicationContext applicationContext;

    @Value("${spring.application.name:unknown}")
    private String serviceName;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("开始扫描服务 [{}] 的Controller接口权限映射...", serviceName);
        
        try {
            Map<String, String> permissionMappings = scanControllers();
            
            if (permissionMappings.isEmpty()) {
                log.warn("服务 [{}] 未扫描到任何权限映射", serviceName);
                return;
            }
            
            // 存储到 Redis
            saveToRedis(permissionMappings);
            
            log.info("服务 [{}] 权限映射扫描完成，共扫描到 {} 个接口权限映射", serviceName, permissionMappings.size());
            
        } catch (Exception e) {
            log.error("服务 [{}] 权限映射扫描失败", serviceName, e);
        }
    }

    /**
     * 扫描所有 Controller 接口
     */
    private Map<String, String> scanControllers() {
        Map<String, String> permissionMappings = new HashMap<>();
        
        // 获取所有Controller Bean
        Map<String, Object> controllers = applicationContext.getBeansWithAnnotation(RestController.class);
        Map<String, Object> controllerBeans = applicationContext.getBeansWithAnnotation(org.springframework.stereotype.Controller.class);
        controllers.putAll(controllerBeans);
        
        for (Object controller : controllers.values()) {
            Class<?> controllerClass = controller.getClass();
            
            // 获取 Controller 级别的 RequestMapping
            RequestMapping classMapping = AnnotatedElementUtils.findMergedAnnotation(controllerClass, RequestMapping.class);
            String basePath = "";
            if (classMapping != null && classMapping.value().length > 0) {
                basePath = classMapping.value()[0];
            }
            
            // 扫描Controller的所有方法
            Method[] methods = controllerClass.getDeclaredMethods();
            for (Method method : methods) {
                // 检查方法是否有@SaCheckPermission注解
                SaCheckPermission permissionAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, SaCheckPermission.class);
                if (permissionAnnotation == null) {
                    continue;
                }
                
                // 获取权限标识（@SaCheckPermission的value是String数组）
                String[] permissions = permissionAnnotation.value();
                if (permissions == null || permissions.length == 0 || !StringUtils.hasText(permissions[0])) {
                    continue;
                }
                String permission = permissions[0]; // 取第一个权限标识
                
                // 获取方法上的请求映射注解
                String methodPath = getMethodPath(method);
                if (methodPath == null) {
                    continue;
                }
                
                // 构建完整的 URI
                String fullUri = buildFullUri(basePath, methodPath);
                
                // 存储映射关系
                permissionMappings.put(fullUri, permission);
                
                log.debug("扫描到权限映射 - URI: {}, 权限: {}", fullUri, permission);
            }
        }
        
        return permissionMappings;
    }

    /**
     * 获取方法的请求路径
     */
    private String getMethodPath(Method method) {
        // 检查各种请求映射注解
        GetMapping getMapping = AnnotatedElementUtils.findMergedAnnotation(method, GetMapping.class);
        if (getMapping != null && getMapping.value().length > 0) {
            return getMapping.value()[0];
        }
        
        PostMapping postMapping = AnnotatedElementUtils.findMergedAnnotation(method, PostMapping.class);
        if (postMapping != null && postMapping.value().length > 0) {
            return postMapping.value()[0];
        }
        
        PutMapping putMapping = AnnotatedElementUtils.findMergedAnnotation(method, PutMapping.class);
        if (putMapping != null && putMapping.value().length > 0) {
            return putMapping.value()[0];
        }
        
        DeleteMapping deleteMapping = AnnotatedElementUtils.findMergedAnnotation(method, DeleteMapping.class);
        if (deleteMapping != null && deleteMapping.value().length > 0) {
            return deleteMapping.value()[0];
        }
        
        RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
        if (requestMapping != null && requestMapping.value().length > 0) {
            return requestMapping.value()[0];
        }
        
        return null;
    }

    /**
     * 构建完整的 URI
     */
    private String buildFullUri(String basePath, String methodPath) {
        // 规范化路径
        basePath = normalizePath(basePath);
        methodPath = normalizePath(methodPath);
        
        // 合并路径
        if (basePath.isEmpty()) {
            return methodPath;
        }
        
        if (methodPath.isEmpty()) {
            return basePath;
        }
        
        // 确保路径之间只有一个斜杠
        if (basePath.endsWith("/") && methodPath.startsWith("/")) {
            return basePath + methodPath.substring(1);
        } else if (!basePath.endsWith("/") && !methodPath.startsWith("/")) {
            return basePath + "/" + methodPath;
        } else {
            return basePath + methodPath;
        }
    }

    /**
     * 规范化路径
     */
    private String normalizePath(String path) {
        if (!StringUtils.hasText(path)) {
            return "";
        }
        
        // 移除末尾的斜杠（除非是根路径）
        path = path.trim();
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        
        // 确保以斜杠开头
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        
        return path;
    }

    /**
     * 保存权限映射到 Redis
     * 使用 Lua 脚本保证原子性：删除旧映射和保存新映射在一个原子操作中完成
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void saveToRedis(Map<String, String> permissionMappings) {
        if (permissionMappings.isEmpty()) {
            return;
        }

        try {
            // 准备 Lua 脚本
            DefaultRedisScript<List> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/SavePermissionMapping.lua")));
            redisScript.setResultType(List.class);

            // 准备参数
            String pattern = RedisCacheKey.apiPermissionMappingPattern(serviceName);
            List<String> keys = Collections.singletonList(pattern);

            // 准备参数列表：key1, value1, key2, value2, ...
            // 使用StringRedisTemplate确保key和value都使用String序列化，避免JSON序列化导致key带引号
            List<String> args = new ArrayList<>();
            for (Map.Entry<String, String> entry : permissionMappings.entrySet()) {
                String uri = entry.getKey();
                String permission = entry.getValue();
                String redisKey = RedisCacheKey.apiPermissionMapping(serviceName, uri);
                
                args.add(redisKey);
                args.add(permission);
                
                log.debug("准备保存权限映射 - Key: {}, Value: {}", redisKey, permission);
            }

            // 使用StringRedisTemplate执行Lua脚本，确保key和value都使用String序列化
            // StringRedisTemplate默认使用StringRedisSerializer，不会对String进行JSON序列化
            org.springframework.data.redis.core.StringRedisTemplate stringRedisTemplate = 
                    new org.springframework.data.redis.core.StringRedisTemplate(
                            defaultRedisTemplate.getConnectionFactory());
            
            // 执行 Lua 脚本
            List<Long> result = stringRedisTemplate.execute(redisScript, keys, (Object[]) args.toArray(new String[0]));
            
            if (result != null && result.size() >= 2) {
                Long deletedCount = result.get(0);
                Long savedCount = result.get(1);
                log.info("服务 [{}] 的权限映射已保存到Redis - 删除旧映射: {} 条, 保存新映射: {} 条", 
                        serviceName, deletedCount, savedCount);
            } else {
                log.warn("服务 [{}] 的权限映射保存完成，但未获取到执行结果", serviceName);
            }

        } catch (Exception e) {
            log.error("使用Lua脚本保存权限映射失败，降级为普通方式保存", e);
            // 降级处理：使用普通方式保存
            saveToRedisFallback(permissionMappings);
        }
    }

    /**
     * 降级处理：使用普通方式保存权限映射
     */
    private void saveToRedisFallback(Map<String, String> permissionMappings) {
        // 先删除该服务的旧映射（使用模式匹配）
        String pattern = RedisCacheKey.apiPermissionMappingPattern(serviceName);
        deleteOldMappings(pattern);
        
        // 使用StringRedisTemplate保存，确保value也使用String序列化，避免JSON序列化
        org.springframework.data.redis.core.StringRedisTemplate stringRedisTemplate = 
                new org.springframework.data.redis.core.StringRedisTemplate(
                        defaultRedisTemplate.getConnectionFactory());
        
        // 保存新映射
        for (Map.Entry<String, String> entry : permissionMappings.entrySet()) {
            String uri = entry.getKey();
            String permission = entry.getValue();
            
            String redisKey = RedisCacheKey.apiPermissionMapping(serviceName, uri);
            stringRedisTemplate.opsForValue().set(redisKey, permission);
            
            log.debug("保存权限映射到Redis - Key: {}, Value: {}", redisKey, permission);
        }
        
        log.info("服务 [{}] 的权限映射已保存到Redis（降级方式），共 {} 条", serviceName, permissionMappings.size());
    }

    /**
     * 删除旧的权限映射（降级方法）
     */
    private void deleteOldMappings(String pattern) {
        try {
            Set<String> keys = defaultRedisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                defaultRedisTemplate.delete(keys);
                log.info("删除服务 [{}] 的旧权限映射，共 {} 条", serviceName, keys.size());
            }
        } catch (Exception e) {
            log.warn("删除旧权限映射失败: {}", e.getMessage());
        }
    }
}

