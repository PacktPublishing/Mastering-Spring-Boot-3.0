package com.packt.ahmeric.reactivesample.controller;


import java.time.Duration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class GreetingController {


    @GetMapping("/greet/{name}")
    public Mono<String> complexGreet(@PathVariable String name) {
        // Creating a complex Mono pipeline
        return Mono.just(name)
                // Prepend the greeting to the name, demonstrating the use of map for transformation
                .map(n -> "Hello, " + n + "! Welcome to Reactive Spring!")
                // Simulate a delay to mimic long-running computation or I/O task
                .delayElement(Duration.ofSeconds(1))
                // Append additional message, demonstrating further transformation
                .map(s -> s + " This is a reactive response.")
                // Log the current state, demonstrating side-effect without altering the stream
                .doOnNext(System.out::println)
                // Append yet another message, demonstrating flatMap for asynchronous operation or chaining
                .flatMap(s -> Mono.just(s + " Enjoy the power of reactive!"))
                // Provide a fallback in case the source Mono is empty
                .switchIfEmpty(Mono.just("Fallback response, in case of empty."))
                // Handle any errors that occur in the pipeline
                .onErrorResume(e -> Mono.just("Error occurred: " + e.getMessage()))
                // Cache the result to avoid re-computation on re-subscription
                .cache()
                // Apply a timeout to the Mono
                .timeout(Duration.ofSeconds(5))
                // Log when the operation is completed, regardless of the outcome
                .doFinally(signalType -> System.out.println("Operation completed with signal: " + signalType));
    }
}
