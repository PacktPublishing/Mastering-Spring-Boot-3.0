package com.packt.ahmeric.reactivesample.controller;

import com.packt.ahmeric.reactivesample.config.SecurityConfig;
import com.packt.ahmeric.reactivesample.model.User;
import com.packt.ahmeric.reactivesample.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.reactive.ReactiveOAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = UserController.class,
        excludeAutoConfiguration = {ReactiveSecurityAutoConfiguration.class,
                ReactiveOAuth2ClientAutoConfiguration.class})
class UserControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private UserRepository userRepository;
    private User testUser;
    @MockBean
    private SecurityWebFilterChain securityWebFilterChain;
    @BeforeEach
    void setUp() {
        testUser = new User(1L, "Test User", "test@example.com");
    }

    @Test
    void getAllUsersTest() {
        when(userRepository.findAll()).thenReturn(Flux.just(testUser));

        webTestClient.get().uri("/users")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class).hasSize(1);
    }
    @Test
    void getUserByIdTest() {
        when(userRepository.findById("1")).thenReturn(Mono.just(testUser));
        webTestClient.get().uri("/users/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("1");
    }
    @Test
    void createUserTest() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Mono.empty());
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(testUser));
        webTestClient.post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(testUser)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("1");
    }
    @Test
    void deleteUserTest() {
        when(userRepository.deleteById("1")).thenReturn(Mono.empty());

        webTestClient.delete().uri("/users/1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void createUserWithExistingEmailTest() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Mono.just(testUser));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(testUser));
        webTestClient.post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(testUser)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void streamUsersTest() {
        when(userRepository.findAll()).thenReturn(Flux.just(testUser));
        webTestClient.get().uri("/users/stream")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class).hasSize(1);
    }
}
