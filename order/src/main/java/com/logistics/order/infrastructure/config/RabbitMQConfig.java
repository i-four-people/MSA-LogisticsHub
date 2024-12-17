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
    public FanoutExchange deadLetterExchange() {
        return new FanoutExchange(properties.getExchange().getDead());
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(properties.getQueues().getDeadLetter())
                .withArgument("x-message-ttl", 60000) // 메시지 TTL: 60초 후 삭제
                .withArgument("x-expires", 120000)    // 큐 TTL: 120초 후 삭제
                .build();
    }

    @Bean
    public Queue productQueue() {
        return new Queue(properties.getQueues().getProduct());
    }

    @Bean
    public Queue deliveryQueue() {
        return QueueBuilder.durable(properties.getQueues().getDelivery())
                .withArgument("x-dead-letter-exchange", properties.getExchange().getDead()) // DLX 설정
                .withArgument("x-max-delivery-count", 3) // 최대 재시도 횟수 설정
                .build();

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

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange());
    }

}
