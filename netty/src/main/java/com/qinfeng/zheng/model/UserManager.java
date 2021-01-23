package com.qinfeng.zheng.model;

import com.qinfeng.zheng.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/23 10:04
 * @dec 用户管理
 */
public final class UserManager {
    private UserManager() {

    }

    /**
     * 用户字典
     */
    private static final Map<Integer, User> userMap = new ConcurrentHashMap<>();


    public static void addUser(User u) {
        if (null != u) {
            userMap.putIfAbsent(u.getUserId(), u);
        }
    }

    public static void removeByUserId(Integer userId) {
        if (userId != null) {
            userMap.remove(userId);
        }
    }

    public static Collection<User> listUser() {
        return userMap.values();
    }


    public static User getUserByUserId(Integer userId) {
        return userMap.get(userId);

    }

}

