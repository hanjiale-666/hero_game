package org.hero.story;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.hero.handler.*;
import org.hero.model.User;
import org.hero.model.UserManager;
import org.hero.msg.GameMsgProtocol;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName GameMsgHandler
 * @Description 处理客户端消息
 * @Author hanjiale
 * @Date 2022/3/28 14:52
 * @Version 1.0
 */
public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Broadcaster.addChannel(ctx.channel());
    }

    /**
     * 用户离场处理的方法
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);

        //先删除当前用户的信道
        Broadcaster.removeChannel(ctx.channel());

        //拿到当前用户的id
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (userId == null) {
            return;
        }

        //在从当前服务器中删除该用户
        UserManager.removeByUserId(userId);

        //构建用户离场的protoBuf协议服务器返回对象
        GameMsgProtocol.UserQuitResult.Builder resultBuilder = GameMsgProtocol.UserQuitResult.newBuilder();
        resultBuilder.setQuitUserId(userId);
        GameMsgProtocol.UserQuitResult newResult = resultBuilder.build();

        //在将用户离场的消息广播出去
        Broadcaster.boradcast(newResult);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("收到客户端消息，msgClazz = " + msg.getClass().getName() + ",msg = " + msg);
        //构建消息对象，并且进行广播群发
        ICmdHandler<? extends GeneratedMessageV3> cmdHandler = CmdHandlerFactory.create(msg.getClass());

        //由多态处理，将指令交给对应的处理器处理
        if (cmdHandler != null){
            cmdHandler.handle(ctx,cast(msg));
        }
    }

    /**
     * 类型转换
     * @param msg
     * @param <T>
     * @return
     */
    private static  <T extends GeneratedMessageV3>T cast(Object msg){
        if (msg == null){
            return null;
        }
        return (T)msg;
    }

}
