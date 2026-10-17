package com.courierpyme.messaging;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

import static com.courierpyme.config.RabbitMQConfig.QUEUE_WAREHOUSE;

@Component
public class WarehouseCommandListener {

    private final ObjectMapper mapper = new ObjectMapper();

    @RabbitListener(queues = QUEUE_WAREHOUSE, ackMode = "MANUAL")
    public void onWarehouseCommand(String body, Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        try {
            CommandEnvelope envelope = mapper.readValue(body, CommandEnvelope.class);
            System.out.println("[WAREHOUSE] type=" + envelope.type() + " eventId=" + envelope.eventId());
            channel.basicAck(tag, false);
        } catch (Exception e) {
            channel.basicNack(tag, false, false);
        }
    }
}