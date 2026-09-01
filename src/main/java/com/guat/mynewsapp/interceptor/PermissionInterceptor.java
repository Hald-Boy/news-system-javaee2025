package com.guat.mynewsapp.interceptor;

import com.guat.mynewsapp.annotation.RequiredRole;
import com.guat.mynewsapp.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.Method;

//权限拦截器
@Slf4j
@Component
public class PermissionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            // 获取被调用方法
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            // 检查方法是否有RequiredRole注解
            RequiredRole requiredRole = method.getAnnotation(RequiredRole.class);
            if (requiredRole != null) {
                // 从注解获取所需角色，例如方法上@RequiredRole(1)
                int role = requiredRole.value();
                // 从请求头中获取 Authorization 字段
                String authHeader = request.getHeader("Authorization");

                // 检查 Authorization 格式是否正确
                if (authHeader != null && authHeader.startsWith("Bearer ")) {

                    // 提取 JWT 令牌（去掉 "Bearer " 前缀）
                    String token = authHeader.substring(7);

                    // 解析JWT
                    Claims claims = JwtUtils.parseJwt(token);
                    int userRole = (int) claims.get("role");

                    // 验证角色权限
                    //当前登录用户的身份 和 访问方法所需的角色比较
                    if (userRole != role) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        return false;
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return false;
                }
            }
        }
        return true;
    }

//    @Override
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        log.info("执行权限控制拦截器的postHandle方法");
//    }

//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        log.info("执行权限控制拦截器的afterCompletion方法");
//    }
}