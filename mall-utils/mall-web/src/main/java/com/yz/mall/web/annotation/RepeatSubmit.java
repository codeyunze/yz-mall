package com.yz.mall.web.annotation;

import java.lang.annotation.*;

/**
 * 防重复提交注解
 *
 * @author yunze
 * @apiNote 防止同一接口同样参数在一个时间段内重复请求提交多次（导致数据重复问题）<br/>
 * 完整策略为：<br/>
 * 1. 前端使用防抖策略，实现一个时间段内只提交一个请求（连续点击提交了三次，但还是只会提交第一次点击的请求）。<br/>
 * 2. 在点击提交按钮之后，前端将按钮置灰，禁止在本次提交请求处理完之前，重复点击。<br/>
 * 3. 后端接口添加重复提交校验，也就是本注解需要做的事情，防止攻击者绕过前端直接操作请求接口。<br/>
 * 4. 在数据库里给表适当的添加唯一索引，防止出现重复数据。<br/>
 * @date 2024/12/29 星期日 20:43
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RepeatSubmit {

    /**
     * 两次有效请求之间的间隔时间，响应一次请求后，后续一段时间（默认5秒）内，重复的请求会被拒绝
     * 单位：秒
     */
    long intervalTime() default 5;

}
