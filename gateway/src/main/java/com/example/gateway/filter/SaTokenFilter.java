package com.example.gateway.filter;


import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Sa-Token 认证过滤器
 * 对请求进行token验证，支持Bearer格式的token
 */
public class SaTokenFilter implements Filter{
    private static final Logger logger = LoggerFactory.getLogger(SaTokenFilter.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 白名单路径 - 这些路径直接放行
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/api/auth/login",
            "/api/user/register"
    );

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestPath = request.getRequestURI();
        System.out.println("请求路径: " + requestPath);

        // 1. 检查是否为白名单路径，直接放行
        if (isWhiteListPath(requestPath)) {
            System.out.println("白名单路径，直接放行: " + requestPath);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 2. 检查请求头中的token
            String authHeader = request.getHeader("Authorization");

            if (!StringUtils.hasText(authHeader)) {
                System.out.println("请求头中没有Authorization");
                writeErrorResponse(response, "缺少Authorization头");
                return;
            }

            // 3. 检查Bearer格式
            if (!authHeader.startsWith("Bearer ")) {
                System.out.println("Authorization格式错误: " + authHeader);
                writeErrorResponse(response, "Authorization格式错误，应为Bearer token格式");
                return;
            }

            // 4. 提取token
            String token = authHeader.substring(7); // 去掉"Bearer "

            if (!StringUtils.hasText(token)) {
                System.out.println("token为空");
                writeErrorResponse(response, "token不能为空");
                return;
            }

            System.out.println("提取到的token: " + token);

            // 5. 验证token合法性和是否过期
            try {
                // 方式一：直接验证token（推荐）
                Object loginId = StpUtil.getLoginIdByToken(token);
                if (loginId == null) {
                    System.out.println("token无效");
                    writeErrorResponse(response, "token无效");
                    return;
                }
                // 方式二：设置当前token到上下文（可选）
                // StpUtil.setLoginId(loginId, token);

                System.out.println("token验证通过，用户ID: " + loginId);

                // token验证通过，继续执行
                filterChain.doFilter(request, response);

            } catch (NotLoginException e) {
                System.out.println("token验证失败: " + e.getMessage());
                String errorMsg = switch (e.getType()) {
                    case NotLoginException.NOT_TOKEN -> "未提供token";
                    case NotLoginException.INVALID_TOKEN -> "token无效";
                    case NotLoginException.TOKEN_TIMEOUT -> "token已过期";
                    case NotLoginException.BE_REPLACED -> "token已被替换";
                    case NotLoginException.KICK_OUT -> "token已被踢下线";
                    default -> "认证失败: " + e.getMessage();
                };
                writeErrorResponse(response, errorMsg);
            }

        } catch (Exception e) {
            System.err.println("过滤器执行异常: " + e.getMessage());
            logger.error("过滤器执行异常: "+e);
            writeErrorResponse(response, "服务器内部错误");
        }
    }

    /**
     * 检查是否为白名单路径
     */
    private boolean isWhiteListPath(String requestPath) {
        // 支持前缀匹配
        return WHITE_LIST.stream().anyMatch(requestPath::startsWith);
    }

    /**
     * 写入错误响应
     */
    private void writeErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        SaResult result = SaResult.error(message).setCode(401);

        String jsonResponse = objectMapper.writeValueAsString(result);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }

    @Override
    public void destroy() {
        System.out.println("SaTokenFilter 销毁");
    }
}