package org.hero.story;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.hero.msg.GameMsgProtocol;

/**
 * @ClassName GameMsgEncoder
 * @Description 消息编码器（服务器-》客户端）
 * @Author hanjiale
 * @Date 2022/3/31 11:35
 * @Version 1.0
 */
@Slf4j
public class GameMsgEncoder extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        //如果消息为null或者消息不能转换成protocol类型的消息，则不处理（GeneratedMessageV3是消息的基类）
        if (msg == null || !(msg instanceof GeneratedMessageV3)){
            super.write(ctx, msg, promise);
            return;
        }

        int msgCode = -1;

        if (msg instanceof GameMsgProtocol.UserEntryResult){
            msgCode = GameMsgProtocol.MsgCode.USER_ENTRY_RESULT_VALUE;
        } else if (msg instanceof GameMsgProtocol.WhoElseIsHereResult){
            msgCode = GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_RESULT_VALUE;
        } else if (msg instanceof GameMsgProtocol.UserMoveToResult){
            msgCode = GameMsgProtocol.MsgCode.USER_MOVE_TO_RESULT_VALUE;
        } else if (msg instanceof GameMsgProtocol.UserQuitResult){
            msgCode = GameMsgProtocol.MsgCode.USER_QUIT_RESULT_VALUE;
        } else {
            log.info("无法识别的消息类型，msgClazz = " + msg.getClass().getName());
            return;
        }

        //将消息转换成字节数组
        byte[] msgBody = ((GeneratedMessageV3) msg).toByteArray();

        //获取netty的bytebuf对象
        ByteBuf buffer = ctx.alloc().buffer();
        //写入消息的长度
        buffer.writeShort((short)0);
        //写入消息编号
        buffer.writeShort((short)msgCode);
        //写入消息体
        buffer.writeBytes(msgBody);

        BinaryWebSocketFrame frame = new BinaryWebSocketFrame(buffer);
        super.write(ctx,frame,promise);
    }
}
