package org.hero.handler;

import com.google.protobuf.GeneratedMessageV3;
import org.hero.msg.GameMsgProtocol;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName CmdHandlerFactory
 * @Description 指令处理器工程类
 * @Author hanjiale
 * @Date 2022/4/2 11:36
 * @Version 1.0
 */
public final class CmdHandlerFactory {

    /**
     * 处理器字典
     */
    private static Map<Class<?>,ICmdHandler<? extends GeneratedMessageV3>> _handlerMap = new HashMap<>();

    /**
     * 私有化，禁止被实例化
     */
    private CmdHandlerFactory(){}

    /**
     * 初始化指令处理器
     */
    public static void init(){
        _handlerMap.put(GameMsgProtocol.UserEntryCmd.class,new UserEntryCmdHandler());
        _handlerMap.put(GameMsgProtocol.WhoElseIsHereCmd.class,new WhoElseIsHereCmdHandler());
        _handlerMap.put(GameMsgProtocol.UserMoveToCmd.class,new UserMoveToCmdHandler());
    }

    /**
     * 创建指令的处理器工厂
     * @param msgClass
     * @return
     */
    public static ICmdHandler<? extends GeneratedMessageV3> create(Class<?> msgClass){
        if (msgClass == null){
            return null;
        }

        return _handlerMap.get(msgClass);
    }
}
