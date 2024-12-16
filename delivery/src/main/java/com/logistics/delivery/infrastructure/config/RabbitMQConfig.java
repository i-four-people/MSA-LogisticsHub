package com.logistics.delivery.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {

    private final RabbitMQProperties properties;

    @Bean
    public FanoutExchange deliveryExchange() {
        return new FanoutExchange(properties.getExchange().getDelivery());
    }

    @Bean
    public Queue orderQueue() {
        return new Queue(properties.getQueues().getOrder());
    }

    @Bean
    public Queue slackQueue() {
        return new Queue(properties.getQueues().getSlack());
    }

    @Bean
    public Binding orderBinding(Queue orderQueue, FanoutExchange deliveryExchange) {
        return BindingBuilder.bind(orderQueue).to(deliveryExchange);
    }

    @Bean
    public Binding slackBinding(Queue slackQueue, FanoutExchange deliveryExchange) {
        return BindingBuilder.bind(slackQueue).to(deliveryExchange);
    }

}
