package com.logistics.delivery.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "message")
public class RabbitMQProperties {

    private Exchange exchange;
    private Queues queues;

    @Getter
    @Setter
    public static class Exchange {
        private String delivery;
    }

    @Getter
    @Setter
    public static class Queues {
        private String order;
    }

}
