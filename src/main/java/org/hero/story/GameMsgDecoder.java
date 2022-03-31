package org.hero.story;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.hero.msg.GameMsgProtocol;

/**
 * @ClassName GameMsgDecoder
 * @Description 消息解码器
 * @Author hanjiale
 * @Date 2022/3/29 9:24
 * @Version 1.0
 */
public class GameMsgDecoder extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //如果消息不能转换为BinaryWebSocketFrame则返回
        if (!(msg instanceof BinaryWebSocketFrame)){
            return;
        }
        //webSocket 二进制消息会通过 HttpServerCodec 解码成BinaryWebSocketFrame 类对象
        BinaryWebSocketFrame frame = (BinaryWebSocketFrame)msg;
        ByteBuf byteBuf = frame.content();

        //读取消息的长度，这里由于socket自己处理 ，所以长度都是0
        byteBuf.readShort();
        //读取消息的编号
        int msgCode = byteBuf.readShort();

        //获取消息体
        byte[]  msgBody = new byte[byteBuf.readableBytes()];
        //将byteBuf中的数据写入到字节数组中
        byteBuf.readBytes(msgBody);

        //protocol消息的基类
        GeneratedMessageV3 message = null;
        switch (msgCode){
            //客户端发送当前登录用户
            case GameMsgProtocol.MsgCode.USER_ENTRY_CMD_VALUE:
                message = GameMsgProtocol.UserEntryCmd.parseFrom(msgBody);
                break;
            //客户端发送获取当前服务器登录的所有用户的指令
            case GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_CMD_VALUE:
                message = GameMsgProtocol.WhoElseIsHereCmd.parseFrom(msgBody);
                break;
            //客户端发送的是登录用户的移动指令
            case GameMsgProtocol.MsgCode.USER_MOVE_TO_CMD_VALUE:
                message = GameMsgProtocol.UserMoveToCmd.parseFrom(msgBody);
                break;
        }

        if (message != null){
            ctx.fireChannelRead(message);
        }
    }
}
