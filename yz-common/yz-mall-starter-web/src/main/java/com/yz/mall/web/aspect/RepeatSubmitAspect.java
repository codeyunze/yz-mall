package com.yz.mall.web.aspect;

import com.yz.mall.web.annotation.RepeatSubmit;
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
import java.util.logging.Logger;

/**
 * 防重复提交AOP切面
 *
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
        String ipAddress = request.getRemoteAddr();
        String realIP = request.getHeader("X-Real-IP");
        log.warning("请求地址：" + ipAddress + "真是地址：" + realIP + "，请求接口" + url);
        if (url.contains("test")) {
            throw new RepeatSubmitException();
        }
        joinPoint.proceed();
        return joinPoint;
    }
}
