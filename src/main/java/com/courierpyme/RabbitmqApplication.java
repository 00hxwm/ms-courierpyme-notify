package com.courierpyme;

import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RabbitmqApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitmqApplication.class, args);
    }

    @Bean
    ApplicationRunner diag(ApplicationContext ctx) {
        return args -> {
            System.out.println("DIAG emailListener="
                + ctx.getBeanNamesForType(com.courierpyme.messaging.EmailCommandListener.class).length);
            System.out.println("DIAG endpointRegistry="
                + ctx.getBeanNamesForType(RabbitListenerEndpointRegistry.class).length);
        };
    }
}