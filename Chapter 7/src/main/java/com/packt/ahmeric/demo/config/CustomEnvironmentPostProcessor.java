package com.packt.ahmeric.demo.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

public class CustomEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // Example: Set a default property if not already set
        String property = environment.getProperty("custom.property", "defaultValue");
        System.setProperty("custom.property", property);
    }
}
