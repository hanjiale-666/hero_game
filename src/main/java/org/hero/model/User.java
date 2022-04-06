package org.hero.model;

/**
 * @ClassName User
 * @Description 用户类
 * @Author hanjiale
 * @Date 2022/3/31 15:54
 * @Version 1.0
 */
public class User {

    /**
     * 用户id
     */
    public int userId;
    /**
     * 英雄形象
     */
    public String heroAvatar;

    /**
     * 当前血量
     */
    public int currHp;

    /**
     * 移动状态
     */
    public final  MoveState moveState = new MoveState();
}
