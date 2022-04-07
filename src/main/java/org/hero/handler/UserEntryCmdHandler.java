package org.hero.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        Integer userId =  (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (userId == null){
            return;
        }

        //获取已存在用户
        User user = UserManager.getUserById(userId);
        if (user == null){
            log.info("用户不存在，userId = {}",userId);
            return;
        }

        //获取英雄形象
        String heroAvatar = user.heroAvatar;

        //构建服务器返回消息对象
        GameMsgProtocol.UserEntryResult.Builder resultBuilder = GameMsgProtocol.UserEntryResult.newBuilder();
        resultBuilder.setUserId(userId);
        resultBuilder.setHeroAvatar(heroAvatar);

        //构建结果并广播群发(在消息发送出去之后，在页面上还是谁也看不见谁，因为这时候消息是一个对象，需要编码码的过程，服务端-》客户端)
        GameMsgProtocol.UserEntryResult newResult = resultBuilder.build();
        Broadcaster.boradcast(newResult);
    }
}
