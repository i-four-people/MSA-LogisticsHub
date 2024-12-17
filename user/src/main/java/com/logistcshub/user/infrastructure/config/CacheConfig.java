package com.logistcshub.user.infrastructure.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

//    private final ObjectMapper objectMapper;
//
//    public CacheConfig(ObjectMapper objectMapper) {
//        this.objectMapper = objectMapper;
//    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {

        // 설정 구성을 먼저 진행한다.
        // Redis 를 이용해서 Spring Cache 를 사용할 때 Redis 관련 설정을 모아두는 클래스
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues() // null 캐싱 여부(null 캐싱하지 않는다)
                .entryTtl(Duration.ofSeconds(10)) // 기본 캐시 유지 시간 (Time to Live)
                .computePrefixWith(CacheKeyPrefix.simple()) // 캐시를 구분하는 접두사 설정
                // 캐시에 저장할 값을 어떻게 직렬화 / 역직렬화 할 것인지 설정
//                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper)));
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.java()));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }
}