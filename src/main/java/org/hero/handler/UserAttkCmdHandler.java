package org.hero.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.hero.msg.GameMsgProtocol;
import org.hero.story.Broadcaster;

/**
 * @ClassName UserAttkCmdHandler
 * @Description 攻击指令处理器
 * @Author hanjiale
 * @Date 2022/4/5 11:57
 * @Version 1.0
 */
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

        //构建掉血指令的返回结果
        GameMsgProtocol.UserSubtractHpResult.Builder resultBuilder2 = GameMsgProtocol.UserSubtractHpResult.newBuilder();
        resultBuilder2.setSubtractHp(10);
        resultBuilder2.setTargetUserId(targetUserId);
        GameMsgProtocol.UserSubtractHpResult newResult2 = resultBuilder2.build();
        //将攻击的掉血效果广播出去
        Broadcaster.boradcast(newResult2);
    }
}
