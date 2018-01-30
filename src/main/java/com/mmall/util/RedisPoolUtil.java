package com.mmall.util;

import com.mmall.common.RedisPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

/**
 * Created by cjq on 2018-01-30 14:44
 */
@Slf4j
public class RedisPoolUtil {

    /**
     * 设置key的有效期，单位是秒
     * @param key
     * @param exTime
     * @return
     */
    public static Long expire(String key , int exTime) {
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.expire(key , exTime);
        } catch (Exception e) {
            log.error("set key:{} value:{} error" , key , e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
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
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.setex(key , exTime ,  value);
        } catch (Exception e) {
            log.error("set key:{} value:{} exTime:{} error" , key , value , exTime , e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static String set(String key , String value) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.set(key , value);
        } catch (Exception e) {
            log.error("set key:{} value:{} error" , key , value , e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static String get(String key) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.get(key);
        } catch (Exception e) {
            log.error("get key:{} error" , key , e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static Long del(String key) {
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.del(key);
        } catch (Exception e) {
            log.error("delete key:{} error" , key , e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static void main(String[] args) {
        Jedis jedis = RedisPool.getJedis();
        RedisPoolUtil.set("KeyTest" , "value");
        String value = RedisPoolUtil.get("KeyTest");
        RedisPoolUtil.setEx("Keyex" , "valueex" , 60 * 10);
        RedisPoolUtil.expire("KeyTest" , 60 * 20);
        RedisPoolUtil.del("KeyTest");

        System.out.println("end");
    }
}
