package org.hero.util;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @ClassName RedisUtil
 * @Description redis工具类
 * @Author hanjiale
 * @Date 2022/4/9 16:04
 * @Version 1.0
 */
@Slf4j
public final class RedisUtil {

    /**
     * redis 连接池
     */
    private static JedisPool jedisPool = null;

    /**
     * 私有化默认构造方法，防止实例化
     */
    private RedisUtil(){
    }

    public static void init(){
        try {
            jedisPool = new JedisPool("192.168.70.149", 6379);
        } catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }

    /**
     * 获取redis实例
     * @return redis实例
     */
    public static Jedis getRedis(){
        if (jedisPool == null){
            throw new RuntimeException("jedispool 尚未初始化好 ");
        }
        final Jedis redis = jedisPool.getResource();
        redis.auth("hjl123456");
        return redis;
    }

}
