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

public class RedisCache3 implements Cache {
    private String name;
    private RedisTemplate<String, Object> redisTemplate;

    public void setName(String name) {
        this.name = name;
    }

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return null;
    }

    @Override
    public ValueWrapper get(final Object key) {
        final String keyf = key.toString();
        Object object = null;
        object = redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] keyfBytes = keyf.getBytes();
                byte[] value = connection.get(keyfBytes);
                if (value == null){
                    return null;
                }
                connection.expire(keyfBytes, 3600);
                return SerializationUtils.deserialize(value);
            }
        });
        ValueWrapper valueWrapper = (object == null ? null : new SimpleValueWrapper(object));
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
        final String keyf = key.toString();
        final Object valurf = value;
        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] keyfBytes = keyf.getBytes();
                byte[] valuebytes = SerializationUtils.serialize((Serializable) valurf);
                connection.set(keyfBytes, valuebytes);
                connection.expire(keyfBytes, 3600);
                return 1L;
            }
        });
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        return null;
    }

    @Override
    public void evict(Object key) {
        final String keyf = key.toString();
        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.del(keyf.getBytes());
                return 1L;
            }
        });
    }

    @Override
    public void clear() {
        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                //清除所有数据
                connection.flushDb();
                return 1L;
            }
        });
    }
}
