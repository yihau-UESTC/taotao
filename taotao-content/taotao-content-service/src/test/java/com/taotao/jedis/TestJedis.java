package com.taotao.jedis;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import redis.clients.jedis.JedisPool;

public class TestJedis {


    @Test
    public void testJedisSpring(){
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-redis.xml");
        JedisClient jedisClient = (JedisClient) context.getBean("jedisClient");
        jedisClient.set("rrr", "11111");
        System.out.println(jedisClient.get("rrr"));

    }
}
