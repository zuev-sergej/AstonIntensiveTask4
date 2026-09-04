package ru.aston.userservice.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventProducer {

    private static final String TOPIC = "user-events";
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    public void send(UserEvent event) {

        kafkaTemplate.send(
                TOPIC,
                event.id().toString(),
                event
        );
    }
}
