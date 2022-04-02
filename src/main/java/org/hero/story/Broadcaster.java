package org.hero.story;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @ClassName Broadcaster
 * @Description 广播类
 * @Author hanjiale
 * @Date 2022/4/1 11:48
 * @Version 1.0
 */
public final class Broadcaster {

    /**
     * 客户端信道数组，一定要使用static ，否则无法实现 群发
     */
    private static final ChannelGroup _channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 私有化，不会被实例化
     */
    private Broadcaster(){}

    /**
     * 添加信道
     * @param channel
     */
    public static void addChannel(Channel channel){
        _channelGroup.add(channel);
    }

    /**
     * 删除信道
     * @param channel
     */
    public static void removeChannel(Channel channel){
        _channelGroup.remove(channel);
    }

    /**
     * 广播消息
     * @param msg
     */
    public static void boradcast(Object msg){
        if (msg == null){
            return;
        }
        _channelGroup.writeAndFlush(msg);
    }
}
