package com.packt.ahmeric.producer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventProducerController {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public EventProducerController(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @GetMapping("/message/{message}")
    public String trigger(@PathVariable String message) {
        kafkaTemplate.send("messageTopic", message);
        return "Hello, Your message has been published: " + message;
    }
}
