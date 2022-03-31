package org.hero.story;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
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

    /**
     * 客户端信道数组，一定要使用static ，否则无法实现 群发
     */
    private static final ChannelGroup _channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 存放当前所有登录的用户
     */
    private static final Map<Integer, User> _userMap = new HashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        _channelGroup.add(ctx.channel());
    }

    /**
     * 用户离场处理的方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);

        //先删除当前用户的信道
        _channelGroup.remove(ctx.channel());

        //拿到当前用户的id
        Integer userId = (Integer)ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (userId == null){
            return;
        }

        //在从当前服务器中删除该用户
        _userMap.remove(userId);

        //构建用户离场的protoBuf协议服务器返回对象
        GameMsgProtocol.UserQuitResult.Builder resultBuilder = GameMsgProtocol.UserQuitResult.newBuilder();
        resultBuilder.setQuitUserId(userId);
        GameMsgProtocol.UserQuitResult newResult = resultBuilder.build();

        //在将用户离场的消息广播出去
        _channelGroup.writeAndFlush(newResult);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("收到客户端消息，msgClazz = " + msg.getClass().getName() + ",msg = " + msg);

        //构建消息对象，并且进行广播群发
        if (msg instanceof GameMsgProtocol.UserEntryCmd){
            //从客户端发出的消息指令中获取userId和英雄形象
            GameMsgProtocol.UserEntryCmd cmd = (GameMsgProtocol.UserEntryCmd)msg;
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
            _userMap.put(user.userId,user);

            //将当前登录用户，附着在当前channel上(为了安全，在发送移动消息的时候不带上用户的id)
            ctx.channel().attr(AttributeKey.valueOf("userId")).set(userId);

            //构建结果并广播群发(在消息发送出去之后，在页面上还是谁也看不见谁，因为这时候消息是一个对象，需要编码码的过程，服务端-》客户端)
            GameMsgProtocol.UserEntryResult newResult = resultBuilder.build();
            _channelGroup.writeAndFlush(newResult);
        } else if (msg instanceof GameMsgProtocol.WhoElseIsHereCmd){
            GameMsgProtocol.WhoElseIsHereResult.Builder resultBuilder = GameMsgProtocol.WhoElseIsHereResult.newBuilder();

            //遍历所有用户
            for (User currUser : _userMap.values()){
                if (currUser == null){
                    continue;
                }

                GameMsgProtocol.WhoElseIsHereResult.UserInfo.Builder userInfoBuilder = GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder();
                userInfoBuilder.setUserId(currUser.userId);
                userInfoBuilder.setHeroAvatar(currUser.heroAvatar);
                resultBuilder.addUserInfo(userInfoBuilder);
            }
            //将所有用户发给当前登录用户
            GameMsgProtocol.WhoElseIsHereResult newResult = resultBuilder.build();
            ctx.writeAndFlush(newResult);
        } else if (msg instanceof GameMsgProtocol.UserMoveToCmd){
            //从信道中获取用户id
            Integer userId = (Integer)ctx.channel().attr(AttributeKey.valueOf("userId")).get();

            if (userId == null){
                return;
            }
            //获取客户端移动信息指令
            GameMsgProtocol.UserMoveToCmd cmd = (GameMsgProtocol.UserMoveToCmd)msg;
            //构造服务端返回对象
            GameMsgProtocol.UserMoveToResult.Builder resultBuilder = GameMsgProtocol.UserMoveToResult.newBuilder();
            resultBuilder.setMoveUserId(userId);
            resultBuilder.setMoveToPosX(cmd.getMoveToPosX());
            resultBuilder.setMoveToPosY(cmd.getMoveToPosY());

            GameMsgProtocol.UserMoveToResult newResult = resultBuilder.build();
            //将当前用户的移动信息广播出去
            _channelGroup.writeAndFlush(newResult);
        }

        //下面的代码是添加解码器之前，获取的是二进制的消息，所以有如下转换，在添加解码器后，消息已经被转换为protocol协议的消息了
        /*//收到的消息是二进制的，需要解码器
        BinaryWebSocketFrame frame = (BinaryWebSocketFrame)msg;
        ByteBuf byteBuf = frame.content();

        //byteBuf.readableBytes()获取字节数组的数量
        byte[]  byteArray = new byte[byteBuf.readableBytes()];
        //将byteBuf中的数据写入到字节数组中
        byteBuf.readBytes(byteArray);

        System.out.println("收到的字节 = ");

        for (byte b : byteArray){
            System.out.print(b);
            System.out.print(",");
        }*/
    }
}
