package com.taotao.utils;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.concurrent.Callable;

public class RedisCache implements Cache {
    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setName(String name) {
        this.name = name;
    }

    private RedisTemplate<String, Object> redisTemplate;
    private String name;

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    private long timeout;

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return this.redisTemplate;
    }

    @Override
    public ValueWrapper get(Object key) {
        System.out.println("----缓存获取------" + key.toString());
        final String keyf = key.toString();
        Object object = null;
        object = redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] key = keyf.getBytes();
                byte[] value = connection.get(key);
                if (value == null){
                    System.out.println("-----缓存不存在------");
                    return null;
                }
                connection.expire(key, timeout);
                return SerializationUtils.deserialize(value);
            }
        });
        ValueWrapper valueWrapper = (object != null ? new SimpleValueWrapper(object) : null);
        System.out.println("----获取到内容----" + valueWrapper);
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
    public void put(Object key, Object value) {
        System.out.println("----加入缓存----; key = " + key + ", value = " + value);
        final String keyString = key.toString();
        final Object valuef = value;
        final long liveTime = timeout;
        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] keyStringBytes = keyString.getBytes();
                byte[] valueb = SerializationUtils.serialize((Serializable) valuef);
                connection.set(keyStringBytes, valueb);
                if (liveTime > 0)connection.expire(keyStringBytes, liveTime);
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
        System.out.println("----缓存删除----");
        final String keyf = key.toString();
        redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.del(keyf.getBytes());
            }
        });
    }

    @Override
    public void clear() {
        System.out.println("----缓存清理---");
        redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                connection.flushDb();
                return "ok";
            }
        });
    }
}
