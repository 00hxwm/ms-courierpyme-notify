package com.courierpyme.messaging;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

import static com.courierpyme.config.RabbitMQConfig.QUEUE_LABEL;

@Component
public class LabelCommandListener {

    private static final Logger log = LoggerFactory.getLogger(LabelCommandListener.class);

    private final ObjectMapper mapper = new ObjectMapper();

    @RabbitListener(queues = QUEUE_LABEL, ackMode = "MANUAL")
    public void onLabelCommand(String body, Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        try {
            CommandEnvelope envelope = mapper.readValue(body, CommandEnvelope.class);
            log.info("[LABEL] type={} eventId={}", envelope.type(), envelope.eventId());
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("[LABEL] Mensaje rechazado, va a la DLQ. Motivo: {}", e.getMessage());
            channel.basicNack(tag, false, false);
        }
    }
}