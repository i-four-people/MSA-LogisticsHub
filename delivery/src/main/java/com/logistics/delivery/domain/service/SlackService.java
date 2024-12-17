package com.logistics.delivery.domain.service;

import com.logistics.delivery.application.dto.event.consume.SlackCreateConsume;

public interface SlackService {
    void createSlackMessage(SlackCreateConsume slackCreateConsume);
}
