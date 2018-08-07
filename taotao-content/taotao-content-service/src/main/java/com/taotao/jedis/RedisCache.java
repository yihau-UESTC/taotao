package com.taotao.jedis;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.concurrent.Callable;

/**
 * 可以根据自己的需要实现对应的方法操作，这里面不同的数据结构得分别实现eg：get和hget
 */
public class RedisCache implements Cache {

    private String name;
    private RedisTemplate<Object, Object> redisTemplate;

    public void setName(String name) {
        this.name = name;
    }

    public void setRedisTemplate(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return this.redisTemplate;
    }

    @Override
    public ValueWrapper get(final Object key) {
        System.out.println("===============get cache===============");
        Object execute = redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] bytes = connection.hGet(name.getBytes(), key.toString().getBytes());
                if (bytes == null) return null;
                Object o = SerializationUtils.deserialize(bytes);
                return o;
            }
        });
        ValueWrapper valueWrapper = execute != null ? new SimpleValueWrapper(execute) : null;
        return valueWrapper;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        return null;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        return null;
    }

    @Override
    public void put(final Object key, final Object value) {
        System.out.println("========================put cache===================");
        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.hSet(name.getBytes(), key.toString().getBytes(),
                        SerializationUtils.serialize((Serializable) value));
                connection.expire(key.toString().getBytes(), 86400);
                return 1L;
            }
        });
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        return null;
    }

    @Override
    public void evict(final Object key) {
        System.out.println("==================del cache========================");
        redisTemplate.execute(new RedisCallback<Void>() {
            @Override
            public Void doInRedis(RedisConnection connection) throws DataAccessException {
                connection.hDel(name.getBytes(), key.toString().getBytes());
                return null;
            }
        });
    }

    @Override
    public void clear() {

    }
}
