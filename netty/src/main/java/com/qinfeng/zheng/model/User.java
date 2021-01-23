package com.qinfeng.zheng.model;

import java.awt.*;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/22 23:56
 * @dec 用户
 */
public class User {
    /**
     * 英雄id
     */
    public int userId;
    /**
     * 英雄形象
     */
    public String heroAvatar;

    /**
     * 用户名
     */
    public String userName;
    /**
     * 血量
     */
    public int curHp = 100;

    public final MoveState moveState = new MoveState();

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
