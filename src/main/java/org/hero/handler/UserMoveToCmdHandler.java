package org.hero.handler;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.hero.msg.GameMsgProtocol;
import org.hero.story.Broadcaster;

/**
 * @ClassName UserMoveToCmdHandler
 * @Description 用户移动指令处理
 * @Author hanjiale
 * @Date 2022/4/1 12:28
 * @Version 1.0
 */
public class UserMoveToCmdHandler implements ICmdHandler<GameMsgProtocol.UserMoveToCmd> {

    /**
     * 将当前登录用户的移动信息广播给，当前服务器所有在场用户，告诉他们，该用户移动到哪里了
     * @param ctx
     * @param msg
     */
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserMoveToCmd msg) {
        //从信道中获取用户id
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();

        if (userId == null) {
            return;
        }
        //获取客户端移动信息指令
        GameMsgProtocol.UserMoveToCmd cmd = msg;
        //构造服务端返回对象
        GameMsgProtocol.UserMoveToResult.Builder resultBuilder = GameMsgProtocol.UserMoveToResult.newBuilder();
        resultBuilder.setMoveUserId(userId);
        resultBuilder.setMoveToPosX(cmd.getMoveToPosX());
        resultBuilder.setMoveToPosY(cmd.getMoveToPosY());

        GameMsgProtocol.UserMoveToResult newResult = resultBuilder.build();
        //将当前用户的移动信息广播出去
        Broadcaster.boradcast(newResult);
    }

}
