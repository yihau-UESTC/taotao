package com.taotao.jedis;

import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class TestJedis {
    @Test
    public void testJedis() {
        Jedis jedis = new Jedis("192.168.0.203", 6379);
        jedis.set("ttt", "123454");
        System.out.println(jedis.get("ttt"));
        jedis.close();
    }

    @Test
    public void testJedisPool() {
        JedisPool jedisPool = new JedisPool("192.168.0.203", 6379);
        Jedis jedis = jedisPool.getResource();
        System.out.println(jedis.get("ttt"));
        jedis.close();
        jedisPool.close();

    }
}
