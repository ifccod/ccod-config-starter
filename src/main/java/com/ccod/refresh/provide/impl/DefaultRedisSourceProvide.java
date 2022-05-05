package com.ccod.refresh.provide.impl;

import com.ccod.refresh.provide.CustomSourceProvide;
import com.ccod.refresh.util.IoUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisClientConfig;

import java.util.Map;

/**
 * redis默认实现
 *
 * @author ccod
 * @date 2022/4/28 11:29 AM
 **/
public class DefaultRedisSourceProvide implements CustomSourceProvide {

    private Jedis jedis;

    private ConfigurableEnvironment environment;

    /**
     * redis数据源key，hash结构
     */
    public static final String REDIS_REFRESH_KEU = "spring:config:map";

    @Override
    public Map<String, Object> getSource() {
        Map res = getJedis().hgetAll(REDIS_REFRESH_KEU);
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
        IoUtils.close(jedis);
    }

    private Jedis getJedis() {
        if (this.jedis == null) {
            this.init();
        }
        return this.jedis;
    }

    @Override
    public ConfigurableEnvironment getEnvironment() {
        return environment;
    }
}
