package com.packt.ahmeric.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

    @Value("${app.message:Hello from Spring Boot!}")
    private String message;

    @Value("${api.key:not very secure}")
    private String apikey;

    @GetMapping("/message")
    public String getMessage() {
        return message;
    }

    @GetMapping("/apikey")
    public String getApikey() {
        return apikey;
    }
}
