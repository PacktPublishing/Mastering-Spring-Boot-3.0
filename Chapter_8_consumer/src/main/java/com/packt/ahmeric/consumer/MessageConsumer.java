package com.packt.ahmeric.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {

    @KafkaListener(topics = "messageTopic", groupId = "consumer_1_id")
    public void listen(String message) {
        System.out.println("Received message: " + message);
    }
}
