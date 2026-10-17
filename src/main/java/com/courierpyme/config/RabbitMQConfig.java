package com.courierpyme.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_DIRECT = "cmd.direct";
    public static final String EXCHANGE_TOPIC = "cmd.topic";
    public static final String EXCHANGE_DLX = "cmd.dead.dlx";

    public static final String QUEUE_EMAIL = "q.cmd.email";
    public static final String QUEUE_WAREHOUSE = "q.cmd.warehouse";
    public static final String QUEUE_LABEL = "q.cmd.label";

    public static final String QUEUE_EMAIL_DLQ = QUEUE_EMAIL + ".dlq";
    public static final String QUEUE_WAREHOUSE_DLQ = QUEUE_WAREHOUSE + ".dlq";
    public static final String QUEUE_LABEL_DLQ = QUEUE_LABEL + ".dlq";

    public static final String RK_EMAIL = "email.send";
    public static final String RK_WAREHOUSE = "warehouse.ticket";
    public static final String RK_LABEL = "label.gen";

    public static final String TOPIC_EMAIL = "email.*";
    public static final String TOPIC_WAREHOUSE = "warehouse.#";
    public static final String TOPIC_LABEL = "label.*";

    @Bean
    public DirectExchange cmdDirectExchange() {
        return new DirectExchange(EXCHANGE_DIRECT, true, false);
    }

    @Bean
    public TopicExchange cmdTopicExchange() {
        return new TopicExchange(EXCHANGE_TOPIC, true, false);
    }

    @Bean
    public DirectExchange cmdDeadLetterExchange() {
        return new DirectExchange(EXCHANGE_DLX, true, false);
    }

    @Bean
    public Queue emailQueue() {
        return workQueue(QUEUE_EMAIL, QUEUE_EMAIL_DLQ);
    }

    @Bean
    public Queue warehouseQueue() {
        return workQueue(QUEUE_WAREHOUSE, QUEUE_WAREHOUSE_DLQ);
    }

    @Bean
    public Queue labelQueue() {
        return workQueue(QUEUE_LABEL, QUEUE_LABEL_DLQ);
    }

    @Bean
    public Queue emailDlq() {
        return QueueBuilder.durable(QUEUE_EMAIL_DLQ).build();
    }

    @Bean
    public Queue warehouseDlq() {
        return QueueBuilder.durable(QUEUE_WAREHOUSE_DLQ).build();
    }

    @Bean
    public Queue labelDlq() {
        return QueueBuilder.durable(QUEUE_LABEL_DLQ).build();
    }

    @Bean
    public Binding emailDirectBinding() {
        return BindingBuilder.bind(emailQueue()).to(cmdDirectExchange()).with(RK_EMAIL);
    }

    @Bean
    public Binding warehouseDirectBinding() {
        return BindingBuilder.bind(warehouseQueue()).to(cmdDirectExchange()).with(RK_WAREHOUSE);
    }

    @Bean
    public Binding labelDirectBinding() {
        return BindingBuilder.bind(labelQueue()).to(cmdDirectExchange()).with(RK_LABEL);
    }

    @Bean
    public Binding emailTopicBinding() {
        return BindingBuilder.bind(emailQueue()).to(cmdTopicExchange()).with(TOPIC_EMAIL);
    }

    @Bean
    public Binding warehouseTopicBinding() {
        return BindingBuilder.bind(warehouseQueue()).to(cmdTopicExchange()).with(TOPIC_WAREHOUSE);
    }

    @Bean
    public Binding labelTopicBinding() {
        return BindingBuilder.bind(labelQueue()).to(cmdTopicExchange()).with(TOPIC_LABEL);
    }

    @Bean
    public Binding emailDlqBinding() {
        return BindingBuilder.bind(emailDlq()).to(cmdDeadLetterExchange()).with(QUEUE_EMAIL_DLQ);
    }

    @Bean
    public Binding warehouseDlqBinding() {
        return BindingBuilder.bind(warehouseDlq()).to(cmdDeadLetterExchange()).with(QUEUE_WAREHOUSE_DLQ);
    }

    @Bean
    public Binding labelDlqBinding() {
        return BindingBuilder.bind(labelDlq()).to(cmdDeadLetterExchange()).with(QUEUE_LABEL_DLQ);
    }

    private Queue workQueue(String name, String dlqName) {
        return QueueBuilder.durable(name)
                .withArgument("x-dead-letter-exchange", EXCHANGE_DLX)
                .withArgument("x-dead-letter-routing-key", dlqName)
                .build();
    }
}