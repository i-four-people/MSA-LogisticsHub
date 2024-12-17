package com.logistcshub.user.infrastructure.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.logistcshub.user.presentation.response.user.UserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.text.SimpleDateFormat;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.password}")
    private String redisPassword;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration(redisHost, redisPort);
        serverConfig.setPassword(redisPassword); // Redis 서버 비밀번호 설정

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder().build();

        return new LettuceConnectionFactory(serverConfig, clientConfig);
    }

//    @Bean
//    public ObjectMapper objectMapper() {
//        PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator
//                .builder()
//                .allowIfSubType("com.logistcshub")
//                .build();
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.activateDefaultTyping(typeValidator, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY); // 모든 객체와 비구현체에 대해 타입을 추가
//        objectMapper.findAndRegisterModules(); // 다른 필요한 모듈 자동 등록
//        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 날짜를 타임스탬프로 직렬화하지 않도록 설정
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        objectMapper.registerModule(new JavaTimeModule()); // Java 8 날짜/시간 지원
//        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")); // 날짜 형식 설정 (ISO-8601 형식)
//
//        return objectMapper;
//    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
//        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper()));
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());


        return template;
    }
}
