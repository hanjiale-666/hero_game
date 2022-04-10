package org.hero.mq;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

/**
 * @ClassName MQProducer
 * @Description 消息队列的生产者
 * @Author hanjiale
 * @Date 2022/4/9 17:55
 * @Version 1.0
 */
@Slf4j
public final class MQProducer {

    /**
     * 生产者
     */
    private static DefaultMQProducer _producer = null;

    /**
     * 私有化默认构造器
     */
    private MQProducer(){

    }

    /**
     * 初始化
     */
    public static void  init(){
        try{

            DefaultMQProducer producer = new DefaultMQProducer("herostroy");
            producer.setNamesrvAddr("192.168.70.149:9876");
            producer.start();
            producer.setRetryTimesWhenSendAsyncFailed(3);

            _producer = producer;
        } catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }

    /**
     * 发送消息
     * @param topic 主题
     * @param msg 消息对象
     */
    public static void sendMsg(String topic,Object msg){
        if (topic == null || msg == null){
            return;
        }
        if (_producer == null){
            throw new RuntimeException("生产者尚未初始化");
        }

        Message mqMsg = new Message();
        mqMsg.setTopic(topic);
        mqMsg.setBody(JSONObject.toJSONBytes(msg));

        try {
           // _producer.createTopic("hero","Victor",0);
            final SendResult result = _producer.send(mqMsg);
            System.out.printf("%s%n",result);
            System.out.println("++++++++++++++++++"+result);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    }


}
