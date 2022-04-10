package org.hero.async;

import lombok.extern.slf4j.Slf4j;
import org.hero.story.MainThreadProcessor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ClassName AsyncOperationService
 * @Description 一部操作处理器
 * @Author hanjiale
 * @Date 2022/4/8 10:08
 * @Version 1.0
 */
@Slf4j
public final class AsyncOperationService {

    /**
     * 单线程线程池数组,不可以为静态的(因为该单线程池数组要在构造方法中使用所以要放在构造单例对象的的语句前面)
     */
    private static final ExecutorService[] _esArray = new ExecutorService[8];
    /**
     * 单例对象
     */
    private static final AsyncOperationService _instance = new AsyncOperationService();

    /**
     * 私有化默认构造器
     */
    private AsyncOperationService(){
        for (int i = 0;i < _esArray.length;i ++){
            //线程名称
            final String threadName = "AsyncOperationProcessor" + i;
            //创建单线程数组
            _esArray[i] = Executors.newSingleThreadExecutor((newRunnable) ->{
                Thread newThread = new Thread(newRunnable);
                newThread.setName(threadName);
                return newThread;
            });
        }
    }

    /**
     * 获取单例对象
     * @return 一部操作处理器
     */
    public static AsyncOperationService getInstance(){
        return _instance;
    }

    /**
     * 处理异步操作
     * @param asyncOp
     */
    public void process(IAsyncOperation asyncOp){
        if (asyncOp == null){
            return;
        }
        //根据bindId 获取线程索引
        int bindId = Math.abs(asyncOp.bindId());
        int esIndex = bindId % _esArray.length;
        _esArray[esIndex].submit(() ->{
            try {
                //先处理异步操作
                asyncOp.doAsync();

              //  Thread.sleep(5000);

                MainThreadProcessor.getInstance().process(() -> {
                    //异步操作完成后，获取异步操作的结果，在回到主线程继续执行
                    asyncOp.doFinish();
                });
                //下面这种写法也行，返回主线程执行完成逻辑
                //MainThreadProcessor.getInstance().process(asyncOp :: doFinish);
            } catch (Exception e){
                log.error(e.getMessage(),e);
            }
        });
    }


}
