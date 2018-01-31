package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by cjq on 2018-01-31 14:24
 */
public class RedisSharedPool {
    private static ShardedJedisPool pool;
    private static Integer maxTotal = PropertiesUtil.getInteger("redis.max.total" , "20");
    private static Integer maxIdle = PropertiesUtil.getInteger("redis.max.idle" , "10");
    private static Integer minIdle = PropertiesUtil.getInteger("redis.min.idle" , "2");

    private static Boolean testOnBorrow = PropertiesUtil.getBoolean("redis.test.borrow","true");//在borrow一个jedis实例的时候，是否要进行验证操作，如果赋值true。则得到的jedis实例肯定是可以用的。
    private static Boolean testOnReturn = PropertiesUtil.getBoolean("redis.test.return","true");//在return一个jedis实例的时候，是否要进行验证操作，如果赋值true。则放回jedispool的jedis实例肯定是可以用的

    private static String redis1Ip = PropertiesUtil.getProperty("redis1.ip");
    private static Integer redis1Port = Integer.parseInt(PropertiesUtil.getProperty("redis1.port"));

    private static String redis2Ip = PropertiesUtil.getProperty("redis2.ip");
    private static Integer redis2Port = Integer.parseInt(PropertiesUtil.getProperty("redis2.port"));

    private static void initPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(maxIdle);
        config.setMaxTotal(maxTotal);
        config.setMinIdle(minIdle);

        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);

        config.setBlockWhenExhausted(true);//连接耗尽的时候，是否阻塞，false会抛出异常，true阻塞直到超时。默认为true。

        List<JedisShardInfo> jedisShardInfoList = new ArrayList<>(2);
        jedisShardInfoList.add(new JedisShardInfo(redis1Ip , redis1Port , 2 * 1000));
        jedisShardInfoList.add(new JedisShardInfo(redis2Ip , redis2Port , 2 * 1000));
        // 采用一致性hash算法
        pool = new ShardedJedisPool(config , jedisShardInfoList , Hashing.MURMUR_HASH , ShardedJedis.DEFAULT_KEY_TAG_PATTERN);
    }

    static {
        initPool();
    }

    public static ShardedJedis getShardedJedis() {
        return pool.getResource();
    }

    public static void returnBrokenResource(ShardedJedis shardedJedis) {
        pool.returnBrokenResource(shardedJedis);
    }

    public static void returnResource(ShardedJedis shardedJedis){
        pool.returnResource(shardedJedis);
    }

    public static void main(String[] args) {
        ShardedJedis shardedJedis = pool.getResource();
        for(int i = 0 ; i < 10 ; i++) {
            shardedJedis.set("key" + i , "value" + i);
        }

        pool.destroy();//临时调用，销毁连接池中的所有连接
        System.out.println("program is end");


    }
}
