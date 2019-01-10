package com.mars.mars_uam.config;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis 存储session配置
 * Created by wuketao on 2018/1/22.
 */
//@Configuration
public class RedisSessionConfig {

    @Value("${spring.redis.host}")
    private String redisHost;
    @Value("${spring.redis.port}")
    private Integer redisPort;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.redis.database}")
    private Integer redisDatabase;
    @Value("${spring.redis.pool.max-idle}")
    private Integer maxIdle;
    @Value("${spring.redis.pool.max-total}")
    private Integer maxTotal;
    @Value("${spring.redis.pool.max-wait-millis}")
    private Integer maxWaitMillis;

    @Bean
    public static ConfigureRedisAction configureRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }

    @Bean
    public JedisConnectionFactory connectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(redisHost);
        factory.setPort(redisPort);
        factory.setUsePool(true);
        factory.setDatabase(redisDatabase);
        if (!Strings.isNullOrEmpty(password)) {
            factory.setPassword(password);
        }
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMaxTotal(maxTotal);
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        factory.setPoolConfig(jedisPoolConfig);
        return factory;
    }
}
