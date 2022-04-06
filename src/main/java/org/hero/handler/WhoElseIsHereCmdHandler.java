package org.hero.handler;

import io.netty.channel.ChannelHandlerContext;
import org.hero.model.MoveState;
import org.hero.model.User;
import org.hero.model.UserManager;
import org.hero.msg.GameMsgProtocol;

/**
 * @ClassName WhoElseIsHereCmdHandler
 * @Description 还有那个用户已经在现场，指令处理
 * @Author hanjiale
 * @Date 2022/4/1 12:25
 * @Version 1.0
 */
public class WhoElseIsHereCmdHandler implements ICmdHandler<GameMsgProtocol.WhoElseIsHereCmd> {

    /**
     * 获取当前服务器所有登录用户列表，发送给当前登录用户
     * @param ctx
     * @param msg
     */
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.WhoElseIsHereCmd msg) {
        GameMsgProtocol.WhoElseIsHereResult.Builder resultBuilder = GameMsgProtocol.WhoElseIsHereResult.newBuilder();

        //遍历所有用户
        for (User currUser : UserManager.listUser()) {
            if (currUser == null) {
                continue;
            }

            GameMsgProtocol.WhoElseIsHereResult.UserInfo.Builder userInfoBuilder = GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder();
            userInfoBuilder.setUserId(currUser.userId);
            userInfoBuilder.setHeroAvatar(currUser.heroAvatar);

            //构建用户移动状态
            MoveState moveState = currUser.moveState;
            GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.Builder moveUserBuilder =
                    GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.newBuilder();
            moveUserBuilder.setFromPosX(moveState.fromPosX);
            moveUserBuilder.setFromPosY(moveState.fromPosY);
            moveUserBuilder.setToPosX(moveState.toPosX);
            moveUserBuilder.setToPosY(moveState.toPosY);
            moveUserBuilder.setStartTime(moveState.startTime);
            //就移动状态设置给用户信息
            userInfoBuilder.setMoveState(moveUserBuilder);

            resultBuilder.addUserInfo(userInfoBuilder);
        }
        //将所有用户发给当前登录用户
        GameMsgProtocol.WhoElseIsHereResult newResult = resultBuilder.build();
        ctx.writeAndFlush(newResult);
    }
}
