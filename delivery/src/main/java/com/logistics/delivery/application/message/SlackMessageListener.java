package com.logistics.delivery.application.message;

import com.logistics.delivery.application.dto.event.EventType;
import com.logistics.delivery.application.dto.event.consume.SlackCreateConsume;
import com.logistics.delivery.application.util.EventUtil;
import com.logistics.delivery.domain.service.SlackService;
import com.logistics.delivery.presentation.auth.AuthContext;
import com.logistics.delivery.presentation.auth.AuthHeaderInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class SlackMessageListener {

    private final SlackService slackService;

    @RabbitListener(queues = "${message.queues.slack}")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSlackCreateEvent(String message) {
        EventType eventType = EventUtil.extractEventType(message);
        log.info("handleSlackCreateEvent eventType: {}", eventType);
        switch (eventType) {
            case SLACK_CREATED -> {
                // 슬랙 메시지 생성 로직 호출
                SlackCreateConsume slackCreateConsume = EventUtil.deserializeEvent(message, SlackCreateConsume.class);
                AuthContext.set(new AuthHeaderInfo(slackCreateConsume.userId(), slackCreateConsume.role()));
                slackService.createSlackMessage(slackCreateConsume);
            }
        }
    }

}
