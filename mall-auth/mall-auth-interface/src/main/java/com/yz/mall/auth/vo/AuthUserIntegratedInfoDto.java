package com.yz.mall.auth.vo;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户综合信息
 *
 * @author yunze
 * @date 2024/8/3 09:30
 */
@Data
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class AuthUserIntegratedInfoDto implements Serializable {

    private final static long serialVersionUID = 1L;

    /**
     * 登录用户Id
     */
    private Long userId;

    /**
     * 登录用户
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 登录有效期
     */
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    private LocalDateTime expires;

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 用户角色
     */
    private List<String> roles;

    /**
     * 用户资源权限（按钮）
     */
    private List<String> permissions;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 用户加入的组织
     */
    private List<OrgInfo> organizations;
}
