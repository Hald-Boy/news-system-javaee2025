package com.guat.mynewsapp.utils;

import com.guat.mynewsapp.entity.Result;
import com.guat.mynewsapp.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 从请求域获取用户id
 */
public class UserContext {

    /**
     * 可选获取用户ID：拿不到返回null，不抛异常
     * 接口支持游客访问，登录才拿到用户 id，不登录也可以正常访问接口。
     * 举例：查看帖子详情。游客可以看；登录用户看的时候额外标记是否点赞。
     * @param request
     * @return
     */
    public static Integer getUserId(HttpServletRequest request) {
        // 从请求域读取拦截器存放的 userId
        // 如果用户**没登录** → 返回null
        // 如果用户**已经登录** → 返回存入的用户 id 对象
        Object uid = request.getAttribute("userId");
        return uid == null ? null : Integer.valueOf(uid.toString());
    }
    /**
     * 必须获取用户ID：拿不到直接抛出未登录异常
     */
    public static Integer requireUserId(HttpServletRequest request) {
        //内部先调用上面的getUserId();
        Integer userId = getUserId(request);
        if (userId == null) throw new BusinessException(Result.CODE_UNAUTHORIZED, "请先登录");
        return userId;
    }

}
