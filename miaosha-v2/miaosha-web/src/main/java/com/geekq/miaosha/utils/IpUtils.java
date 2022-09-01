package com.geekq.miaosha.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取IP
 *
 * @author chenh
 * @version 1.0
 * @date 2022/1/7 15:20
 **/
public class IpUtils {

    /**
     * 获取远程IP
     *
     * @param request 请求
     * @return {@link String}
     * @author chenh
     * @date 2022/1/7 15:21
     **/
    public static String getRemoteIp(HttpServletRequest request) {
        String remoteIp = null;
        remoteIp = request.getHeader("X-Forwarded-For");
        if (remoteIp == null || remoteIp.isEmpty() || "unknown".equalsIgnoreCase(remoteIp)) {
            remoteIp = request.getHeader("X-Real-IP");
        }
        if (remoteIp == null || remoteIp.isEmpty() || "unknown".equalsIgnoreCase(remoteIp)) {
            remoteIp = request.getHeader("Proxy-Client-IP");
        }
        if (remoteIp == null || remoteIp.isEmpty() || "unknown".equalsIgnoreCase(remoteIp)) {
            remoteIp = request.getHeader("WL-Proxy-Client-IP");
        }
        if (remoteIp == null || remoteIp.isEmpty() || "unknown".equalsIgnoreCase(remoteIp)) {
            remoteIp = request.getHeader("HTTP_CLIENT_IP");
        }
        if (remoteIp == null || remoteIp.isEmpty() || "unknown".equalsIgnoreCase(remoteIp)) {
            remoteIp = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (remoteIp == null || remoteIp.isEmpty() || "unknown".equalsIgnoreCase(remoteIp)) {
            remoteIp = request.getRemoteAddr();
        }
        if (remoteIp == null || remoteIp.isEmpty() || "unknown".equalsIgnoreCase(remoteIp)) {
            remoteIp = request.getRemoteHost();
        }
        // 如果是多级代理，那么取第一个ip为客户ip
        if (remoteIp != null && remoteIp.indexOf(",") != -1) {
            remoteIp = remoteIp.substring(0, remoteIp.indexOf(",")).trim();
        }
        return remoteIp;
    }

}
