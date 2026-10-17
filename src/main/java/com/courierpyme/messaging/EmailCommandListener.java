package com.courierpyme.messaging;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

import static com.courierpyme.config.RabbitMQConfig.QUEUE_EMAIL;

@Component
public class EmailCommandListener {

    private static final Logger log = LoggerFactory.getLogger(EmailCommandListener.class);

    private final ProcessedEventRegistry processed;
    private final ObjectMapper mapper = new ObjectMapper();

    public EmailCommandListener(ProcessedEventRegistry processed) {
        this.processed = processed;
    }

    @RabbitListener(queues = QUEUE_EMAIL, ackMode = "MANUAL")
    public void onEmailCommand(String body, Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        try {
            CommandEnvelope envelope = mapper.readValue(body, CommandEnvelope.class);
            String eventId = envelope.eventId();

            if (eventId == null || eventId.isBlank()) {
                throw new IllegalArgumentException("eventId ausente");
            }
            if (processed.contains(eventId)) {
                log.info("[EMAIL] Duplicado ignorado eventId={}", eventId);
                channel.basicAck(tag, false);
                return;
            }
            log.info("[EMAIL] type={} eventId={} traceId={} payload={}",
                    envelope.type(), eventId, envelope.traceId(), envelope.payload());
            processed.add(eventId);
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("[EMAIL] Fallo, enviando a DLQ: {}", e.getMessage(), e);
            channel.basicNack(tag, false, false);
        }
    }
}