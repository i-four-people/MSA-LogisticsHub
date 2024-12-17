package com.logistics.delivery.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
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

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAdviceChain(RetryInterceptorBuilder.stateless()
                .maxAttempts(3) // 최대 재시도 횟수 설정
                .recoverer(new RejectAndDontRequeueRecoverer()) // 실패 시 메시지 처리 중단
                .build());
        return factory;
    }

}
