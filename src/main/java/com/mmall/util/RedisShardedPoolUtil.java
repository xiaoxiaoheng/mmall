package com.mmall.util;

import com.mmall.common.RedisSharedPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.ShardedJedis;



/**
 * Created by cjq on 2018-01-30 14:44
 */
@Slf4j
public class RedisShardedPoolUtil {

    /**
     * 设置key的有效期，单位是秒
     * @param key
     * @param exTime
     * @return
     */
    public static Long expire(String key , int exTime) {
        ShardedJedis jedis = null;
        Long result = null;
        try {
            jedis = RedisSharedPool.getShardedJedis();
            result = jedis.expire(key , exTime);
        } catch (Exception e) {
            log.error("set key:{} value:{} error" , key , e);
            RedisSharedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisSharedPool.returnResource(jedis);
        return result;
    }

    /**
     * 设置键值，并且设置过期时间
     * @param key
     * @param value
     * @param exTime  秒
     * @return
     */
    public static String setEx(String key , String value , int exTime) {
        ShardedJedis jedis = null;
        String result = null;
        try {
            jedis = RedisSharedPool.getShardedJedis();
            result = jedis.setex(key , exTime ,  value);
        } catch (Exception e) {
            log.error("set key:{} value:{} exTime:{} error" , key , value , exTime , e);
            RedisSharedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisSharedPool.returnResource(jedis);
        return result;
    }

    public static String set(String key , String value) {
        ShardedJedis jedis = null;
        String result = null;
        try {
            jedis = RedisSharedPool.getShardedJedis();
            result = jedis.set(key , value);
        } catch (Exception e) {
            log.error("set key:{} value:{} error" , key , value , e);
            RedisSharedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisSharedPool.returnResource(jedis);
        return result;
    }

    public static String get(String key) {
        ShardedJedis jedis = null;
        String result = null;
        try {
            jedis = RedisSharedPool.getShardedJedis();
            result = jedis.get(key);
        } catch (Exception e) {
            log.error("get key:{} error" , key , e);
            RedisSharedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisSharedPool.returnResource(jedis);
        return result;
    }

    public static Long del(String key) {
        ShardedJedis jedis = null;
        Long result = null;
        try {
            jedis = RedisSharedPool.getShardedJedis();
            result = jedis.del(key);
        } catch (Exception e) {
            log.error("delete key:{} error" , key , e);
            RedisSharedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisSharedPool.returnResource(jedis);
        return result;
    }

    public static void main(String[] args) {
        ShardedJedis jedis = RedisSharedPool.getShardedJedis();
        RedisShardedPoolUtil.set("KeyTest" , "value");
        String value = RedisShardedPoolUtil.get("KeyTest");
        RedisShardedPoolUtil.setEx("Keyex" , "valueex" , 60 * 10);
        RedisShardedPoolUtil.expire("KeyTest" , 60 * 20);
        RedisShardedPoolUtil.del("KeyTest");

        System.out.println("end");
    }
}
