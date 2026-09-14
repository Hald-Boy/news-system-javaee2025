package com.guat.mynewsapp.interceptor;

import com.guat.mynewsapp.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import io.jsonwebtoken.SignatureException;

@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override   //目标资源运行前运行，返回：true放行，返回：false不放行
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){

        System.out.println("执行拦截器的preHandle方法");
        //1.获取请求的url
        String url = request.getRequestURL().toString();
        log.info("url:{}",url);

//        //2.判断请求的url是否含有login，如果有说明是登录操作，直接放行
//        if(url.endsWith("/login")){
//            log.info("是登录操作，可以放行");
//            //放行！
//            return true; //让代码不再继续执行
//        }

        // 3. 从请求头中获取 Authorization 字段
        String authHeader = request.getHeader("Authorization");

        // 4.检查 Authorization 格式是否正确
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            throw new JwtException("缺少有效的认证信息");
        }

        // 5. 提取 JWT 令牌（去掉 "Bearer " 前缀）
        String token = authHeader.substring(7);
        log.info("token:{}",token);

        try {
            //6.解析jwt
            Claims claims = JwtUtils.parseJwt(token);

            // 7. 将用户信息存入请求域，供后续控制器使用
            request.setAttribute("userId", claims.get("id"));
            request.setAttribute("username", claims.get("username"));
            request.setAttribute("role", claims.get("role"));

            // 8. 验证通过，放行请求
            return true;
        } catch (ExpiredJwtException e) {
            // 9. 捕获令牌过期异常
            throw new JwtException("令牌已过期");
        } catch (SignatureException e) {
            // 10. 捕获签名验证失败异常
            throw new JwtException("签名验证失败");
        } catch (Exception e) {
            // 11. 捕获其他 JWT 解析异常
            throw new JwtException("无效的令牌");
        }
    }
}
