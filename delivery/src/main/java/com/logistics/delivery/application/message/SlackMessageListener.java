package com.logistics.delivery.application.message;

import com.logistics.delivery.application.dto.event.EventType;
import com.logistics.delivery.application.dto.event.consume.SlackCreateConsume;
import com.logistics.delivery.application.util.EventUtil;
import com.logistics.delivery.domain.service.SlackService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SlackMessageListener {

    private final SlackService slackService;

    @RabbitListener(queues = "${message.queues.slack}")
    public void handleSlackCreateEvent(String message) {
        EventType eventType = EventUtil.extractEventType(message);
        switch (eventType) {
            case SLACK_CREATED -> {
                // 슬랙 메시지 생성 로직 호출
                SlackCreateConsume slackCreateConsume = EventUtil.deserializeEvent(message, SlackCreateConsume.class);
                slackService.createSlackMessage(slackCreateConsume);
            }
        }
    }

}
