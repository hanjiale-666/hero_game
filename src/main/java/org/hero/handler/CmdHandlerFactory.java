package org.hero.handler;

import com.google.protobuf.GeneratedMessageV3;
import lombok.extern.slf4j.Slf4j;
import org.hero.msg.GameMsgProtocol;
import org.hero.util.PackageUtil;

import javax.swing.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName CmdHandlerFactory
 * @Description 指令处理器工程类
 * @Author hanjiale
 * @Date 2022/4/2 11:36
 * @Version 1.0
 */
@Slf4j
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

        //获取当前文件的包名称
        final String packageName = CmdHandlerFactory.class.getPackage().getName();
        //获取ICmdHandler以及他的子类
        Set<Class<?>> clazzSet = PackageUtil.listSubClazz(packageName, true, ICmdHandler.class);
        for (Class<?> clazz : clazzSet){
            if ((clazz.getModifiers() & Modifier.ABSTRACT) != 0){
                //如果是抽象类,以及接口过滤掉
                continue;
            }
            //获取当前类的所有方法
            Method[] methodArray = clazz.getDeclaredMethods();
            //消息的类型
            Class<?> msgType = null;
            for (Method currMethod : methodArray){
                if (!currMethod.getName().equals("handle")){
                    //如果不是handle方法，查看下一个方法
                    continue;
                }
                //获取方法的参数类型（class1.isAssignableFrom(class2)方法是判断，class2是不是class1的子类或者子接口）
                Class<?>[] parameterTypes = currMethod.getParameterTypes();
                if (parameterTypes.length != 2 || parameterTypes[1] == GeneratedMessageV3.class ||
                    !GeneratedMessageV3.class.isAssignableFrom(parameterTypes[1])){
                    continue;
                }
                msgType = parameterTypes[1];
            }

            if (msgType == null){
                continue;
            }
            //创建指令处理器
            try {
                ICmdHandler<?> handler = (ICmdHandler<?>)clazz.newInstance();
                log.info("{} <=======> {}",msgType.getName(),clazz.getName());

                _handlerMap.put(msgType,handler);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }


        //上面的代码取代下面这个代码
       /* _handlerMap.put(GameMsgProtocol.UserEntryCmd.class,new UserEntryCmdHandler());
        _handlerMap.put(GameMsgProtocol.WhoElseIsHereCmd.class,new WhoElseIsHereCmdHandler());
        _handlerMap.put(GameMsgProtocol.UserMoveToCmd.class,new UserMoveToCmdHandler());*/
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
