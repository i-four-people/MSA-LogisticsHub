package com.logistics.order.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {

    private final RabbitMQProperties properties;

    @Bean
    public FanoutExchange orderExchange() {
        return new FanoutExchange(properties.getExchange().getOrder());
    }

    @Bean
    public Queue productQueue() {
        return new Queue(properties.getQueues().getProduct());
    }

    @Bean
    public Queue deliveryQueue() {
        return new Queue(properties.getQueues().getDelivery());
    }

    // 상품 큐와 Exchange 바인딩
    @Bean
    public Binding productBinding(Queue productQueue, FanoutExchange orderExchange) {
        return BindingBuilder.bind(productQueue).to(orderExchange);
    }

    // 배송 큐와 Exchange 바인딩
    @Bean
    public Binding deliveryBinding(Queue deliveryQueue, FanoutExchange orderExchange) {
        return BindingBuilder.bind(deliveryQueue).to(orderExchange);
    }

}
