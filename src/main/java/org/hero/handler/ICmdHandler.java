package org.hero.handler;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;

/**
 * 指令处理器接口
 * GameMsgProtocol.UserEntryCmd,GameMsgProtocol.UserMoveToCmd,GameMsgProtocol.WhoElseIsHereCmd
 * 这三个的指令都继承自GeneratedMessageV3，所以用泛型来代替传入的参数，实现多态
 */
public interface ICmdHandler<T extends GeneratedMessageV3> {

    /**
     * 处理指令
     * @param ctx
     * @param cmd
     */
    void handle(ChannelHandlerContext ctx,T cmd);
}
