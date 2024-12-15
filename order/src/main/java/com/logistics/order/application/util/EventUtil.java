package com.logistics.order.application.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.order.application.dto.event.EventType;

public class EventUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static EventType extractEventType(String message) {
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            String eventType = jsonNode.get("eventType").asText();

            return EventType.valueOf(eventType);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract EventType from message", e);
        }
    }

    public static <T> T deserializeEvent(String message, Class<T> valueType) {
        try {
            return objectMapper.readValue(message, valueType);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize message to " + valueType.getSimpleName(), e);
        }
    }

}
