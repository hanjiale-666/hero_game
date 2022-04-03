package org.hero.story;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import lombok.extern.slf4j.Slf4j;
import org.hero.msg.GameMsgProtocol;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName GameMsgRecognizer
 * @Description 消息识别器
 * @Author hanjiale
 * @Date 2022/4/3 11:12
 * @Version 1.0
 */
@Slf4j
public final class GameMsgRecognizer {

    /**
     * 消息代码和消息体字典
     */
    private static final Map<Integer, GeneratedMessageV3> _msgCodeAndMsgBodyMap = new HashMap<>();

    /**
     * 消息类型和消息编号字典
     */
    private static final Map<Class<?>,Integer> _msgClazzAndMsgCodeMap = new HashMap<>();

    /**
     * 私有化类默认构造器
     */
    public GameMsgRecognizer(){}

    public static void init(){
        //获取GameMsgProtocol内部定义的所有类
        Class<?>[] innerClazzArray = GameMsgProtocol.class.getDeclaredClasses();

        for (Class<?> innerClazz : innerClazzArray){
            //如果不是GeneratedMessageV3以及他的子类，不做处理
            if (!GeneratedMessageV3.class.isAssignableFrom(innerClazz)){
                continue;
            }

            String className = innerClazz.getSimpleName();
            className = className.toLowerCase();

            //MsgCode是个枚举类
            for (GameMsgProtocol.MsgCode msgCode : GameMsgProtocol.MsgCode.values()){
                String strMsgCode = msgCode.name();
                strMsgCode = strMsgCode.replace("_", "");
                strMsgCode = strMsgCode.toLowerCase();

                if (!strMsgCode.startsWith(className)){
                    continue;
                }

                try {
                    log.info("{} <====> {}",msgCode.getNumber(),innerClazz.getName());
                    //反射调用getDefaultInstance方法
                    Object returnObj = innerClazz.getDeclaredMethod("getDefaultInstance").invoke(innerClazz);
                    _msgCodeAndMsgBodyMap.put(msgCode.getNumber(),(GeneratedMessageV3)returnObj);

                    _msgClazzAndMsgCodeMap.put(innerClazz,msgCode.getNumber());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }

            }
        }

        //上面的代码通过反射的方式实现下面的功能
       /* _msgCodeAndMsgBodyMap.put(GameMsgProtocol.MsgCode.USER_ENTRY_CMD_VALUE,GameMsgProtocol.UserEntryCmd.getDefaultInstance());
        _msgCodeAndMsgBodyMap.put(GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_CMD_VALUE,GameMsgProtocol.WhoElseIsHereCmd.getDefaultInstance());
        _msgCodeAndMsgBodyMap.put(GameMsgProtocol.MsgCode.USER_MOVE_TO_CMD_VALUE,GameMsgProtocol.UserMoveToCmd.getDefaultInstance());

        _msgClazzAndMsgCodeMap.put(GameMsgProtocol.UserEntryResult.class,GameMsgProtocol.MsgCode.USER_ENTRY_RESULT_VALUE);
        _msgClazzAndMsgCodeMap.put(GameMsgProtocol.WhoElseIsHereResult.class,GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_RESULT_VALUE);
        _msgClazzAndMsgCodeMap.put(GameMsgProtocol.UserMoveToResult.class,GameMsgProtocol.MsgCode.USER_MOVE_TO_RESULT_VALUE);
        _msgClazzAndMsgCodeMap.put(GameMsgProtocol.UserQuitResult.class,GameMsgProtocol.MsgCode.USER_QUIT_RESULT_VALUE);*/
    }



    /**
     * 获取消息的构建者
     * @param msgCode
     * @return
     */
    public static Message.Builder getBuilderByMsgCode(int msgCode){
        if (msgCode < 0){
            return null;
        }
        GeneratedMessageV3 msg = _msgCodeAndMsgBodyMap.get(msgCode);
        if (msg == null){
            return null;
        }
        return msg.newBuilderForType();
    }

    /**
     * 获取消息编号
     * @param msgClazz
     * @return
     */
    public static int getMsgCodeByMsgClazz(Class<?> msgClazz){
        if (msgClazz == null){
            return -1;
        }

        Integer msgCode = _msgClazzAndMsgCodeMap.get(msgClazz);
        if (msgCode == null){
            return -1;
        } else {
            return msgCode.intValue();
        }
    }
}
