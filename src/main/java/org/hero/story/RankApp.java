package org.hero.story;

import lombok.extern.slf4j.Slf4j;
import org.hero.mq.MqConsumer;
import org.hero.util.RedisUtil;

/**
 * @ClassName RankApp
 * @Description 排行榜应用程序
 * @Author hanjiale
 * @Date 2022/4/10 11:08
 * @Version 1.0
 */
@Slf4j
public class RankApp {

    /**
     * 应用入口函数
     * @param args
     */
    public static void main(String[] args) {
        RedisUtil.init();
        MqConsumer.init();
        log.info("排行榜应用程序启动成功！");
    }
}
