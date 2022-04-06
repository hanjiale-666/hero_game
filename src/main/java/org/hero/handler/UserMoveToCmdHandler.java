package org.hero.handler;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.hero.model.MoveState;
import org.hero.model.User;
import org.hero.model.UserManager;
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
        if (ctx == null || msg == null){
            return;
        }
        //从信道中获取用户id
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (userId == null) {
            return;
        }

        //获取移动用户
        User moveUser = UserManager.getUserById(userId);
        //获取移动状态
        MoveState moveState = moveUser.moveState;
        //设置起始位置和开始移动时间
        moveState.fromPosX = msg.getMoveFromPosX();
        moveState.fromPosY = msg.getMoveFromPosY();
        moveState.toPosX = msg.getMoveToPosX();
        moveState.toPosY = msg.getMoveToPosY();
        moveState.startTime = System.currentTimeMillis();

        //获取客户端移动信息指令
        GameMsgProtocol.UserMoveToCmd cmd = msg;
        //构造服务端返回对象
        GameMsgProtocol.UserMoveToResult.Builder resultBuilder = GameMsgProtocol.UserMoveToResult.newBuilder();
        resultBuilder.setMoveUserId(userId);
        resultBuilder.setMoveFromPosX(moveState.fromPosX);
        resultBuilder.setMoveFromPosY(moveState.fromPosY);
        resultBuilder.setMoveToPosX(moveState.toPosX);
        resultBuilder.setMoveToPosY(moveState.toPosY);
        resultBuilder.setMoveStartTime(moveState.startTime);

        GameMsgProtocol.UserMoveToResult newResult = resultBuilder.build();
        //将当前用户的移动信息广播出去
        Broadcaster.boradcast(newResult);
    }

}
