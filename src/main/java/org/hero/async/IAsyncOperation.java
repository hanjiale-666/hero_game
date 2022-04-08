package org.hero.async;

/**
 * 异步操作接口
 */
public interface IAsyncOperation {

    /**
     * 获取绑定id
     * @return 绑定id
     */
    default int bindId(){
        return 0;
    }

    /**
     * 执行异步操作
     */
    void doAsync();

    /**
     * 执行完成逻辑,java8实现这个接口不用必须实现下面这个方法了
     */
    default void doFinish(){

    }
}
