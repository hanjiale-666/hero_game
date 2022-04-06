package org.hero.model;

/**
 * @ClassName MoveState
 * @Description 玩家移动 状态
 * @Author hanjiale
 * @Date 2022/4/6 14:51
 * @Version 1.0
 */
public class MoveState {

    /**
     * 起始位置x
     */
    public float fromPosX;
    /**
     * 起始位置y
     */
    public float fromPosY;
    /**
     * 目标位置x
     */
    public float toPosX;
    /**
     * 目标位置y
     */
    public float toPosY;

    /**
     * 移动开始时间
     */
    public long startTime;
}
