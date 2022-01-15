package com.mock.apimocks.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfig {
    private final String hostname;
    private final Integer port;
    private final String password;

    public RedisConfig(@Value("${redis.host}") String hostname,
                       @Value("${redis.port}") String port,
                       @Value("${redis.password}") String password) {
        this.hostname = hostname;
        this.port = Integer.parseInt(port);
        this.password = password;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }

    private JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(this.hostname, this.port);
        redisStandaloneConfiguration.setPassword(RedisPassword.of(this.password));
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }
}
