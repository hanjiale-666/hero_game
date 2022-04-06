package org.hero.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.hero.model.User;
import org.hero.model.UserManager;
import org.hero.msg.GameMsgProtocol;
import org.hero.story.Broadcaster;
import org.hero.story.GameMsgEncoder;

import javax.crypto.NullCipher;

/**
 * @ClassName UserAttkCmdHandler
 * @Description 攻击指令处理器
 * @Author hanjiale
 * @Date 2022/4/5 11:57
 * @Version 1.0
 */
@Slf4j
public class UserAttkCmdHandler implements ICmdHandler<GameMsgProtocol.UserAttkCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserAttkCmd cmd) {
        if (ctx == null || cmd == null){
            return;
        }
        //获取被攻击者id
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (userId == null){
            return;
        }
        //获取被攻击者id
        int targetUserId = cmd.getTargetUserId();

        //构建攻击指令返回结果
        GameMsgProtocol.UserAttkResult.Builder resultBuilder = GameMsgProtocol.UserAttkResult.newBuilder();
        resultBuilder.setAttkUserId(userId);
        resultBuilder.setTargetUserId(targetUserId);
        GameMsgProtocol.UserAttkResult newResult = resultBuilder.build();
        //将攻击指令广播出去
        Broadcaster.boradcast(newResult);

        //获取被攻击用户
        User targetUser = UserManager.getUserById(targetUserId);
        if (targetUser == null){
            return;
        }

        log.info("当前线程 = {}，当前用户的血量 = {}",Thread.currentThread().getName(),targetUser.currHp);
        int subtractHp = 10;
        targetUser.currHp = targetUser.currHp - subtractHp;

        //广播减血消息
        broadcastSubtractHp(targetUserId,subtractHp);

        /**
         * 如果血量为零，广播死亡消息
         */
        if (targetUser.currHp <= 0){
            broadcastDie(targetUserId);
        }
    }

    /**
     * 广播减血消息
     * @param targetUserId 目标消息id
     * @param subtractHp 减血量
     */
    public static void broadcastSubtractHp(int targetUserId,int subtractHp){
        if (targetUserId <= 0 || subtractHp == 0) {
            return;
        }
        //构建掉血指令的返回结果
        GameMsgProtocol.UserSubtractHpResult.Builder resultBuilder2 = GameMsgProtocol.UserSubtractHpResult.newBuilder();
        resultBuilder2.setSubtractHp(subtractHp);
        resultBuilder2.setTargetUserId(targetUserId);
        GameMsgProtocol.UserSubtractHpResult newResult2 = resultBuilder2.build();
        //将攻击的掉血效果广播出去
        Broadcaster.boradcast(newResult2);
    }

    /**
     * 广播死亡消息
     * @param targetUserId
     */
    private static void broadcastDie(int targetUserId){
        if (targetUserId <=0){
            return;
        }

        GameMsgProtocol.UserDieResult.Builder result = GameMsgProtocol.UserDieResult.newBuilder();
        result.setTargetUserId(targetUserId);
        GameMsgProtocol.UserDieResult di = result.build();
        Broadcaster.boradcast(di);
    }
}
