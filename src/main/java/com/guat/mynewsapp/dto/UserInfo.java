package com.guat.mynewsapp.dto;

import com.guat.mynewsapp.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 对外返回的用户信息（不含密码）
 */
@Data
@NoArgsConstructor
public class UserInfo {

    private Integer id;
    private String username;
    private String userAccount;
    private String phone;
    private String avatar;
    private String cover;
    private String bio;
    private String location;
    private LocalDate birthday;
    private Integer totalLikeCount;
    private Integer followCount;
    private Integer fanCount;
    private Integer role;

    public static UserInfo from(User user) {
        if (user == null) {
            return null;
        }
        UserInfo info = new UserInfo();
        info.setId(user.getId());
        info.setUsername(user.getUsername());
        info.setUserAccount(user.getUserAccount());
        info.setPhone(user.getPhone());
        info.setAvatar(user.getAvatar());
        info.setCover(user.getCover());
        info.setBio(user.getBio());
        info.setLocation(user.getLocation());
        info.setBirthday(user.getBirthday());
        info.setTotalLikeCount(user.getTotalLikeCount());
        info.setFollowCount(user.getFollowCount());
        info.setFanCount(user.getFanCount());
        info.setRole(user.getRole());
        return info;
    }
}
