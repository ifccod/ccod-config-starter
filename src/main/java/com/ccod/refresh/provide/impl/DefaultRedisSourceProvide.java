package com.ccod.refresh.provide.impl;

import com.ccod.refresh.properties.CustomRefreshContext;
import com.ccod.refresh.provide.CustomSourceProvide;
import com.google.common.collect.Lists;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisClientConfig;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * redis默认实现
 *
 * @author ccod
 * @date 2022/4/28 11:29 AM
 **/
public class DefaultRedisSourceProvide implements CustomSourceProvide {

    private Jedis jedis;

    private ConfigurableEnvironment environment;

    @Override
    public Map<String, Object> getSource() {
        Map res = getJedis().hgetAll(CustomRefreshContext.REDIS_REFRESH_KEU);
        return res;
    }

    @Override
    public List<String> refresh() {
        PropertySource propertySource = this.environment.getPropertySources().get(CustomRefreshContext.SOURCE_NAME);
        if (propertySource == null) {
            return null;
        }
        List<String> res = Lists.newArrayList();
        Map<String, Object> source = (Map) propertySource.getSource();
        Map<String, Object> redisSourceMap = getSource();
        if (CollectionUtils.isEmpty(redisSourceMap)) {
            return null;
        }
        redisSourceMap.forEach((redisSourceKey, redisSourceValue) -> {
            Object sourceValue = source.get(redisSourceKey);
            if (!Objects.equals(sourceValue, redisSourceValue)) {
                source.put(redisSourceKey, redisSourceValue);
                res.add(redisSourceKey);
            }
        });
        return res;
    }

    @Override
    public void setEnvironment(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    public void init() {
        String host = environment.getRequiredProperty("spring.redis.host", String.class);
        Integer database = environment.getRequiredProperty("spring.redis.database", Integer.class);
        Integer port = environment.getRequiredProperty("spring.redis.port", Integer.class);
        String password = environment.getRequiredProperty("spring.redis.password", String.class);
        JedisClientConfig jedisClientConfig = DefaultJedisClientConfig.builder().password(password).database(database).build();
        this.jedis = new Jedis(new HostAndPort(host, port), jedisClientConfig);
        this.jedis.connect();
    }

    @Override
    public void close() {
        if (jedis != null) {
            try {
                jedis.close();
            } catch (Exception e) {
                // skip
            }
        }
    }

    private Jedis getJedis() {
        if (this.jedis == null) {
            this.init();
        }
        return this.jedis;
    }
}
