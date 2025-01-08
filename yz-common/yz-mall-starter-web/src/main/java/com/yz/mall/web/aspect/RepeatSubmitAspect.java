package com.yz.mall.web.aspect;

import cn.hutool.crypto.SecureUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yz.mall.web.annotation.RepeatSubmit;
import com.yz.mall.web.common.RedisCacheKey;
import com.yz.mall.web.common.Result;
import com.yz.mall.web.exception.RepeatSubmitException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * 防重复提交AOP切面
 * @apiNote 实现的核心思想就是多个重复的请求，去竞争同一把锁，竞争到锁的允许正常往下执行，没有竞争到锁的就会被拒绝。<br/>
 * 锁的key是由请求者的IP地址 + 请求接口URI + 请求参数进行组合后，由MD5去生成的摘要加密数据。
 * @author yunze
 * @date 2024/12/29 星期日 21:35
 */
@Aspect
@Component
public class RepeatSubmitAspect {

    private static Logger log = Logger.getLogger(RepeatSubmitAspect.class.getName());

    private final RedisTemplate<String, Object> defaultRedisTemplate;

    public RepeatSubmitAspect(RedisTemplate<String, Object> defaultRedisTemplate) {
        this.defaultRedisTemplate = defaultRedisTemplate;
    }

    /**
     * 切入点为 {@link RepeatSubmit} 注解 <br/>
     * 所有添加了 {@link RepeatSubmit} 注解的方法，都会进入到当前AOP切面里
     */
    @Pointcut(value = "@annotation(repeatSubmit)")
    public void pointcutRepeatSubmit(RepeatSubmit repeatSubmit) {
    }


    @Around(value = "pointcutRepeatSubmit(repeatSubmit)", argNames = "joinPoint,repeatSubmit")
    public Object around(ProceedingJoinPoint joinPoint, RepeatSubmit repeatSubmit) throws Throwable {
        HttpServletRequest request = (HttpServletRequest) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()).resolveReference(RequestAttributes.REFERENCE_REQUEST);

        assert request != null;
        String url = request.getRequestURI();
        String realIP = request.getHeader("X-Real-IP");
        log.warning("真实地址：" + realIP + "，请求接口" + url);

        String information = realIP + "&" + url;

        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            String paramsStr = args[0].toString();
            information = information + "&" + paramsStr;
        }

        String key = RedisCacheKey.repeatSubmit(SecureUtil.md5(information));

        if (Boolean.TRUE.equals(defaultRedisTemplate.hasKey(key))) {
            throw new RepeatSubmitException();
        }

        // 两次有效请求之间的间隔时间
        long intervalTime = repeatSubmit.intervalTime();
        // 记录第一次请求
        Boolean record = defaultRedisTemplate.boundValueOps(key).setIfAbsent(information, intervalTime, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(record)) {
            // 可能存在多个请求同时通过了Boolean.TRUE.equals(defaultRedisTemplate.hasKey(key))判断
            // 所以在进行记录请求时，使用 setIfAbsent ，只允许第一个请求正常运行，后续的请求全部拦截
            throw new RepeatSubmitException();
        }

        // 继续往下执行
        Result<Object> result = (Result) joinPoint.proceed();

        // 执行结束
        return result;
    }
}
