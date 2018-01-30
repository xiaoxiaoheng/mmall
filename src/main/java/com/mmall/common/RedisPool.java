package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by cjq on 2018-01-30 14:14
 */
public class RedisPool {
    private static JedisPool pool;
    private static Integer maxTotal = PropertiesUtil.getInteger("redis.max.total" , "20");
    private static Integer maxIdle = PropertiesUtil.getInteger("redis.max.idle" , "10");
    private static Integer minIdle = PropertiesUtil.getInteger("redis.min.idle" , "2");

    private static Boolean testOnBorrow = PropertiesUtil.getBoolean("redis.test.borrow","true");//在borrow一个jedis实例的时候，是否要进行验证操作，如果赋值true。则得到的jedis实例肯定是可以用的。
    private static Boolean testOnReturn = PropertiesUtil.getBoolean("redis.test.return","true");//在return一个jedis实例的时候，是否要进行验证操作，如果赋值true。则放回jedispool的jedis实例肯定是可以用的

    private static String redisIp = PropertiesUtil.getProperty("redis2.ip");
    private static Integer redisPort = Integer.parseInt(PropertiesUtil.getProperty("redis2.port"));

    private static void initPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(maxIdle);
        config.setMaxTotal(maxTotal);
        config.setMinIdle(minIdle);

        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);

        config.setBlockWhenExhausted(true);//连接耗尽的时候，是否阻塞，false会抛出异常，true阻塞直到超时。默认为true。

        pool = new JedisPool(config,redisIp,redisPort,1000*2);
    }

    static {
        initPool();
    }

    public static Jedis getJedis() {
        return pool.getResource();
    }

    public static void returnBrokenResource(Jedis jedis) {
        pool.returnBrokenResource(jedis);
    }

    public static void returnResource(Jedis jedis){
        pool.returnResource(jedis);
    }

    public static void main(String[] args) {
        Jedis jedis = pool.getResource();
        jedis.set("geelykey","geelyvalue");
        returnResource(jedis);

        pool.destroy();//临时调用，销毁连接池中的所有连接
        System.out.println("program is end");


    }
}
