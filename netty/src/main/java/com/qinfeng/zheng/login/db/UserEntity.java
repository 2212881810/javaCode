package com.qinfeng.zheng.login.db;

public class UserEntity {
    /**
     * 用户 Id
     */
    public int userId;

    /**
     * 用户名称
     */
    public String userName;

    /**
     * 密码
     */
    public String password;

    /**
     * 英雄形象
     */
    public String heroAvatar;

    public UserEntity(int userId, String userName, String password, String heroAvatar) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.heroAvatar = heroAvatar;
    }

    public UserEntity() {
    }
}
