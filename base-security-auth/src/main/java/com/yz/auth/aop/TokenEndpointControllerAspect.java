package com.yz.auth.aop;

import com.yz.common.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Map;

/**
 * @author : yunze
 * @date : 2023/1/29 19:05
 */
// @Component
// @Aspect
@Slf4j
public class TokenEndpointControllerAspect {

    @Around("execution(* org.springframework.security.oauth2.provider.endpoint.TokenEndpoint.postAccessToken(..))")
    public Object handleControllerMethod(ProceedingJoinPoint joinPoint) throws Throwable {

        Object[] args = joinPoint.getArgs();
        Principal principal = (Principal) args[0];
        if (!(principal instanceof Authentication)) {
            throw new InsufficientAuthenticationException(
                    "There is no client authentication. Try adding an appropriate authentication filter.");
        }

        Map<String, String> parameters = (Map<String, String>) args[1];
        String grantType = parameters.get(OAuth2Utils.GRANT_TYPE);

        Object proceed = joinPoint.proceed(args);

        ResponseEntity<OAuth2AccessToken> responseEntity = (ResponseEntity<OAuth2AccessToken>) proceed;
        OAuth2AccessToken body = responseEntity.getBody();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Result.success(body));

    }
}
