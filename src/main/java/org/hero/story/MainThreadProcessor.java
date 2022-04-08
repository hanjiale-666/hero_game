package org.hero.story;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.hero.handler.CmdHandlerFactory;
import org.hero.handler.ICmdHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @ClassName MainThreadProcessor
 * @Description 主线程处理器
 * @Author hanjiale
 * @Date 2022/4/7 8:43
 * @Version 1.0
 */
@Slf4j
public final class MainThreadProcessor {

    /**
     * 单例对象
     */
    private static final MainThreadProcessor instance = new MainThreadProcessor();

    /**
     * 单线程的线程池
     */
    private static final ExecutorService executor = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread1 = new Thread(r);
            thread1.setName("MainThreadProcessor");
            return thread1;
        }
    });

    /**
     * 构造方法私有化，不被实例化
     */
    private MainThreadProcessor(){}

    /**
     * 获取单例对象
     * @return
     */
    public static MainThreadProcessor getInstance(){
        return instance;
    }

    /**
     * 处理消息
     * @param ctx 客户端信道上下文
     * @param msg 消息对象
     */
    public void process(ChannelHandlerContext ctx, GeneratedMessageV3 msg){
        if (ctx == null || msg == null){
            return;
        }
        //单线程处理指令，避免多个线程处理同一个值
        executor.submit(() -> {
            //构建消息对象，并且进行广播群发
            ICmdHandler<? extends GeneratedMessageV3> cmdHandler = CmdHandlerFactory.create(msg.getClass());

            if (cmdHandler != null){
                try {
                    //由多态处理，将指令交给对应的处理器处理
                    cmdHandler.handle(ctx, cast(msg));
                } catch (Exception e){
                    //捕捉错误，异常线程退出
                    log.error(e.getMessage(),e);
                }
            } else {
                log.error("未找到对应的指令处理器，msgClazz = {}",msg.getClass().getName());
            }
        });
    }

    /**
     * 处理消息
     * @param runnable
     */
    public void process(Runnable runnable){
        if (runnable == null){
            return;
        }
        executor.submit(runnable);
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
