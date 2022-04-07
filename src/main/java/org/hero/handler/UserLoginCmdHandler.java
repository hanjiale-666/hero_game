package org.hero.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.hero.login.LoginService;
import org.hero.login.db.UserEntity;
import org.hero.model.User;
import org.hero.model.UserManager;
import org.hero.msg.GameMsgProtocol;

/**
 * @ClassName UserLoginCmdHandler
 * @Description 用户登录指令处理器
 * @Author hanjiale
 * @Date 2022/4/7 10:49
 * @Version 1.0
 */
@Slf4j
public class UserLoginCmdHandler implements ICmdHandler<GameMsgProtocol.UserLoginCmd> {

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserLoginCmd cmd) {
        log.info("用户登录消息，username = {}，password = {}",cmd.getUserName(),cmd.getPassword());
        if (ctx == null ||cmd == null){
            return;
        }

        String userName = cmd.getUserName();
        String password = cmd.getPassword();
        log.info("用户登录：userName = {},password = {}",userName,password);

        //用户登录
        UserEntity userEntity = null;
        userEntity = LoginService.getInstance().userLogin(userName, password);
        if (userEntity == null){
            log.error("用户登录失败，userName = {}",userName);
            return;
        }
        int userId = userEntity.userId;
        String heroAvatar = userEntity.heroAvatar;

        //将每一个登录的用户都保存起来，以便后面登录的用户可以看到之前登录的用户
        User user = new User();
        user.userId = userEntity.userId;
        user.userName = userEntity.userName;
        user.heroAvatar = userEntity.heroAvatar;
        user.currHp = 100;
        UserManager.addUser(user);

        //将当前登录用户，附着在当前channel上(为了安全，在发送移动消息的时候不带上用户的id)
        ctx.channel().attr(AttributeKey.valueOf("userId")).set(userId);

        GameMsgProtocol.UserLoginResult.Builder result = GameMsgProtocol.UserLoginResult.newBuilder();
        result.setUserId(user.userId);
        result.setUserName(user.userName);
        result.setHeroAvatar(user.heroAvatar);
        //构建用户登录指令返回结果
        GameMsgProtocol.UserLoginResult newResult = result.build();
        //告诉当前用户已经登录成功，
        ctx.writeAndFlush(newResult);
    }
}
