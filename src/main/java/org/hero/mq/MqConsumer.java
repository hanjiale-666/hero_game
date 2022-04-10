package org.hero.mq;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.hero.rank.RankService;

import java.util.List;

/**
 * @ClassName MqConsumer
 * @Description 消息队列消费者
 * @Author hanjiale
 * @Date 2022/4/10 10:56
 * @Version 1.0
 */
@Slf4j
public class MqConsumer {

    /**
     * 私有化默认构造器
     */
    private MqConsumer(){

    }

    /**
     * 初始化
     */
    public static void init(){
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("herostroy");
        consumer.setNamesrvAddr("192.168.70.149:9876");

        try {
            consumer.subscribe("Victor","*");
            //注册回调函数
            consumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                    //遍历消息
                    for (MessageExt msgExt : msgs){
                        //解析战斗结果消息
                        VictorMsg mqMsg = JSONObject.parseObject(msgExt.getBody(), VictorMsg.class);
                        log.info("从消息队列里收到战斗结果，winnerId =  {},loserId = {}",mqMsg.winnerId,mqMsg.loserId);

                        RankService.getInstance().refreshRank(mqMsg.winnerId,mqMsg.loserId);
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });
            consumer.start();
        } catch (MQClientException e) {
            log.error(e.getMessage(),e);
        }
    }
}
