package org.hero.story;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.hero.handler.CmdHandlerFactory;
import org.hero.mq.MQProducer;
import org.hero.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @ClassName ServerMain
 * @Description 游戏 后端服务器
 * @Author hanjiale
 * @Date 2022/3/28 10:32
 * @Version 1.0
 */
public class ServerMain {

    /**
     * 日志对象
     */
    static private final Logger log = LoggerFactory.getLogger(ServerMain.class);

    /**
     * 应用主函数
     * @param args
     */
    public static void main(String[] args) {
        //初始化指令处理器字典
        CmdHandlerFactory.init();
        //初始化消息识别器
        GameMsgRecognizer.init();
        //sql回话初始化
        MySqlSessionFactory.init();
        //redis初始化
        RedisUtil.init();
        //生产者初始化
        MQProducer.init();
        //netty代码
        //负责处理客户端连接，有连接建立channel
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //读取客户端的消息
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup,workerGroup);
        b.channel(NioServerSocketChannel.class);
        b.childHandler(new ChannelInitializer<SocketChannel>() {
            //客户端
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(
                        //Http 服务器编解码器（可以保证消息的完整性）
                        new HttpServerCodec(),
                        //内容长度限制
                        new HttpObjectAggregator(65535),
                        //WebSocket 协议处理器, 在这里处理握手、ping、pong 等消息
                        new WebSocketServerProtocolHandler("/websocket"),
                        //解码器，将二进制消息解码成protocol消息类（客户端-》服务端）
                        new GameMsgDecoder(),
                        //编码器，（服务端到客户端）
                        new GameMsgEncoder(),
                        //自定义消息处理器
                        new GameMsgHandler()
                );
            }
        });

        try {
            // 绑定 12345 端口,
            // 注意: 实际项目中会使用 argArray 中的参数来指定端口号
            ChannelFuture f = b.bind(12345).sync();

            if (f.isSuccess()){
                log.info("服务器启动成功");
            }
            // 等待服务器信道关闭,
            // 也就是不要立即退出应用程序, 让应用程序可以一直提供服务
            f.channel().closeFuture().sync();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
