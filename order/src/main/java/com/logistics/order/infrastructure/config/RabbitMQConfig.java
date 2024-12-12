package com.logistics.order.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String ORDER_EXCHANGE = "order.exchange";

    // 생성 관련
    public static final String ORDER_CREATED_QUEUE = "order.created.queue";
    public static final String ORDER_CREATED_ROUTING_KEY = "order.created";

    // 삭제 관련
    public static final String ORDER_DELETED_QUEUE = "order.deleted.queue";
    public static final String ORDER_DELETED_ROUTING_KEY = "order.deleted";


    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(ORDER_EXCHANGE);
    }

    @Bean
    public Queue orderCreatedQueue() {
        return new Queue(ORDER_CREATED_QUEUE);
    }

    @Bean
    public Queue orderDeletedQueue() {
        return new Queue(ORDER_DELETED_QUEUE);
    }

    @Bean
    public Binding orderBinding(Queue orderCreatedQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(orderCreatedQueue).to(orderExchange).with(ORDER_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding orderDeletedBinding(Queue orderDeletedQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(orderDeletedQueue).to(orderExchange).with(ORDER_DELETED_ROUTING_KEY);
    }

}
