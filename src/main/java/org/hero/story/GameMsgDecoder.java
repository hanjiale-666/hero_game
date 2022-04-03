package org.hero.story;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.hero.msg.GameMsgProtocol;

/**
 * @ClassName GameMsgDecoder
 * @Description 消息解码器
 * @Author hanjiale
 * @Date 2022/3/29 9:24
 * @Version 1.0
 */
@Slf4j
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

        //获取消息的构建者
        Message.Builder msgBuilder = GameMsgRecognizer.getBuilderByMsgCode(msgCode);
        if (msgBuilder == null){
            log.error("无法识别的消息类型，msgCode = {}",msgCode);
            return;
        }

        //获取消息体
        byte[]  msgBody = new byte[byteBuf.readableBytes()];
        //将byteBuf中的数据写入到字节数组中
        byteBuf.readBytes(msgBody);

        msgBuilder.clear();
        //将消息合并起来
        msgBuilder.mergeFrom(msgBody);
        Message newMsg = msgBuilder.build();

        if (newMsg != null){
            ctx.fireChannelRead(newMsg);
        }
    }
}
