package org.hero.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName UserManager
 * @Description 用户管理类
 * @Author hanjiale
 * @Date 2022/4/1 12:04
 * @Version 1.0
 */
public final class UserManager {

    /**
     * 存放当前服务器所有登录的用户
     */
    private static final Map<Integer, User> _userMap = new HashMap<>();

    /**
     * 私有化，禁止被实例化
     */
    private UserManager(){}

    /**
     * 添加用户
     * @param user
     */
    public static void addUser(User user){
        if (user != null){
            _userMap.put(user.userId,user);
        }
    }

    /**
     * 根据userId删除用户
     * @param userId
     */
    public static void removeByUserId(int userId){
        _userMap.remove(userId);
    }

    /**
     * 获取当前服务器登录用户的列表
     * @return
     */
    public static Collection<User> listUser(){
        return _userMap.values();
    }

    /**
     * 根据用户id获取用户
     * @param userId
     * @return
     */
    public static User getUserById(int userId){
        return _userMap.get(userId);
    }
}
