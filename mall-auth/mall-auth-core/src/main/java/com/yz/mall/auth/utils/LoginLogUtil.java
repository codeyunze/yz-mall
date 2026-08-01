package com.yz.mall.auth.utils;

import com.yz.mall.base.HeaderConstants;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 登录日志工具类
 *
 * @author yunze
 * @since 2025-12-11
 */
public class LoginLogUtil {

    /**
     * 获取客户端IP地址
     *
     * @param request HTTP请求
     * @return IP地址
     */
    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader(HeaderConstants.USER_IP_HEADER);
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 处理多IP的情况，取第一个IP
        if (StringUtils.hasText(ip) && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        // 处理本地IP的情况
        if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
            try {
                InetAddress inetAddress = InetAddress.getLocalHost();
                ip = inetAddress.getHostAddress();
            } catch (UnknownHostException e) {
                // 忽略异常，使用默认IP
            }
        }
        return ip;
    }

    /**
     * 获取浏览器类型
     *
     * @param request HTTP请求
     * @return 浏览器类型
     */
    public static String getBrowser(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (!StringUtils.hasText(userAgent)) {
            return "未知";
        }
        userAgent = userAgent.toLowerCase();
        if (userAgent.contains("edge")) {
            return "Edge";
        } else if (userAgent.contains("edg")) {
            return "Edge";
        } else if (userAgent.contains("chrome")) {
            return "Chrome";
        } else if (userAgent.contains("safari") && !userAgent.contains("chrome")) {
            return "Safari";
        } else if (userAgent.contains("firefox")) {
            return "Firefox";
        } else if (userAgent.contains("opera") || userAgent.contains("opr")) {
            return "Opera";
        } else if (userAgent.contains("msie") || userAgent.contains("trident")) {
            return "IE";
        } else if (userAgent.contains("micromessenger")) {
            return "微信浏览器";
        } else if (userAgent.contains("qqbrowser")) {
            return "QQ浏览器";
        } else {
            return "未知";
        }
    }

    /**
     * 获取操作系统类型
     *
     * @param request HTTP请求
     * @return 操作系统类型
     */
    public static String getOs(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (!StringUtils.hasText(userAgent)) {
            return "未知";
        }
        userAgent = userAgent.toLowerCase();
        if (userAgent.contains("windows")) {
            return "Windows";
        } else if (userAgent.contains("mac")) {
            return "Mac OS";
        } else if (userAgent.contains("linux")) {
            return "Linux";
        } else if (userAgent.contains("android")) {
            return "Android";
        } else if (userAgent.contains("iphone") || userAgent.contains("ipad") || userAgent.contains("ipod")) {
            return "iOS";
        } else if (userAgent.contains("unix")) {
            return "Unix";
        } else {
            return "未知";
        }
    }

    /**
     * 根据IP地址获取登录地点（简化版，实际项目中可以使用IP地址库）
     *
     * @param ip IP 地址
     * @return 登录地点
     */
    public static String getLoginLocation(String ip) {
        // 这里简化处理，实际项目中可以使用IP地址库（如ip2region、GeoIP等）来获取详细地址
        if (!StringUtils.hasText(ip)) {
            return "未知";
        }
        // 本地 IP
        if ("127.0.0.1".equals(ip) || ip.startsWith("192.168.") || ip.startsWith("10.") || ip.startsWith("172.")) {
            return "内网IP";
        }
        // 这里可以集成第三方 IP 地址查询服务
        return "未知";
    }
}

