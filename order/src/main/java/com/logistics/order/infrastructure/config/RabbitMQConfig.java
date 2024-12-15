package com.logistics.order.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {

    private final RabbitMQProperties properties;

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(properties.getExchange().getOrder());
    }

    @Bean
    public Queue orderCreatedQueue() {
        return new Queue(properties.getQueues().getCreated());
    }

    @Bean
    public Queue orderDeletedQueue() {
        return new Queue(properties.getQueues().getDeleted());
    }

    @Bean
    public Queue orderStatusChangedQueue() {
        return new Queue(properties.getQueues().getStatusChanged());
    }

    @Bean
    public Binding orderCreatedBinding(Queue orderCreatedQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(orderCreatedQueue).to(orderExchange).with(properties.getRoutingKeys().getCreated());
    }

    @Bean
    public Binding orderDeletedBinding(Queue orderDeletedQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(orderDeletedQueue).to(orderExchange).with(properties.getRoutingKeys().getDeleted());
    }

    @Bean
    public Binding orderStatusChangedBinding(Queue orderStatusChangedQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(orderStatusChangedQueue).to(orderExchange).with(properties.getRoutingKeys().getStatusChanged());
    }

}
