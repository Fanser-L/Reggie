package com.fanser.reggie.config;

import org.springframework.cache.annotation.CachingConfigurationSelector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig extends CachingConfigurationSelector {
    @Bean
    public RedisTemplate<Object,Object> redisTemplate(RedisConnectionFactory connectionFactory){
        RedisTemplate<Object,Object> redisTemplate = new RedisTemplate<>();
        //默认的key 序列化器为： JdkSerializationRedis,读取的键值对就是一串字符，不能正常阅读那种
        //学习redis的时候遇到的问题，redis本身的存的和通过java存的键值对不太一样，所以需要配置一下，解决不可读的问题
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }
}
