package com.courierpyme.messaging;

import java.util.Map;

public record CommandEnvelope(
        String type,
        String eventId,
        String timestamp,
        String traceId,
        String correlationId,
        Map<String, Object> payload) {
}