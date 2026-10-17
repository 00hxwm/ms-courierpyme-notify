package com.courierpyme.messaging;

import org.springframework.stereotype.Component;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ProcessedEventRegistry {

    private final Set<String> processed = ConcurrentHashMap.newKeySet();

    public boolean contains(String eventId) {
        return processed.contains(eventId);
    }

    public void add(String eventId) {
        processed.add(eventId);
    }
}