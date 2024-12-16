package com.logistics.delivery.application.message;

import com.logistics.delivery.application.dto.event.EventType;
import com.logistics.delivery.application.dto.event.consume.SlackCreateConsume;
import com.logistics.delivery.application.util.EventUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SlackMessageListener {

    // TODO: private final 해당 부분에 서비스를 추가하시면 될 것 같습니다!

    @RabbitListener(queues = "${message.queues.slack}")
    public void handleSlackCreateEvent(String message) {
        EventType eventType = EventUtil.extractEventType(message);
        switch (eventType) {
            case SLACK_CREATED -> {
                // 슬랙 메시지 생성 로직 호출
                SlackCreateConsume slackCreateConsume = EventUtil.deserializeEvent(message, SlackCreateConsume.class);
                // TODO: 주입받은 서비스의 메서드를 통해서 슬랙 메시지를 만드시면 될 것 같습니다.
            }
        }
    }

}
