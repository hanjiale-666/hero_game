package org.hero.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.hero.model.User;
import org.hero.model.UserManager;
import org.hero.msg.GameMsgProtocol;
import org.hero.story.Broadcaster;

/**
 * @ClassName UserEntryCmdHandler
 * @Description 用户入场处理
 * @Author hanjiale
 * @Date 2022/4/1 12:21
 * @Version 1.0
 */
public class UserEntryCmdHandler implements ICmdHandler<GameMsgProtocol.UserEntryCmd> {

    /**
     * 当前用户登录后，将该用户广播给其他用户
     * @param ctx
     * @param msg
     */
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserEntryCmd msg) {
        //从客户端发出的消息指令中获取userId和英雄形象
        GameMsgProtocol.UserEntryCmd cmd = msg;
        int userId = cmd.getUserId();
        String heroAvatar = cmd.getHeroAvatar();

        //构建服务器返回消息对象
        GameMsgProtocol.UserEntryResult.Builder resultBuilder = GameMsgProtocol.UserEntryResult.newBuilder();
        resultBuilder.setUserId(userId);
        resultBuilder.setHeroAvatar(heroAvatar);

        //将每一个登录的用户都保存起来，以便后面登录的用户可以看到之前登录的用户
        User user = new User();
        user.userId = userId;
        user.heroAvatar = heroAvatar;
        UserManager.addUser(user);

        //将当前登录用户，附着在当前channel上(为了安全，在发送移动消息的时候不带上用户的id)
        ctx.channel().attr(AttributeKey.valueOf("userId")).set(userId);

        //构建结果并广播群发(在消息发送出去之后，在页面上还是谁也看不见谁，因为这时候消息是一个对象，需要编码码的过程，服务端-》客户端)
        GameMsgProtocol.UserEntryResult newResult = resultBuilder.build();
        Broadcaster.boradcast(newResult);
    }
}
