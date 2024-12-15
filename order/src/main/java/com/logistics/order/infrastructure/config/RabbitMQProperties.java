package com.logistics.order.infrastructure.config;

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
    private RoutingKeys routingKeys;

    @Getter @Setter
    public static class Exchange {
        private String order;
    }

    @Getter @Setter
    public static class Queues {
        private String created;
        private String deleted;
    }

    @Getter @Setter
    public static class RoutingKeys {
        private String created;
        private String deleted;
    }

}
