package com.qinfeng.zheng;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/22 23:56
 * @dec 用户
 */
public class User {
    /**
     * 英雄id
     */
    private int userId;
    /**
     * 英雄形象
     */
    private String heroAvatar;


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getHeroAvatar() {
        return heroAvatar;
    }

    public void setHeroAvatar(String heroAvatar) {
        this.heroAvatar = heroAvatar;
    }
}
