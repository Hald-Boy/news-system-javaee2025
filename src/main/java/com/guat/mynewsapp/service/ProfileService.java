package com.guat.mynewsapp.service;

import com.guat.mynewsapp.dto.PageBean;
import com.guat.mynewsapp.dto.PostCardVO;
import com.guat.mynewsapp.dto.UserProfileVO;

public interface ProfileService {

    /** 个人主页：用户信息 + 作品数 + 关注/互关状态 */
    UserProfileVO getProfile(Integer viewerId, Integer userId);

    /** 某用户的作品列表（分页） */
    PageBean<PostCardVO> listUserPosts(Integer userId, int pageNum, int pageSize);
}
