package com.packt.ahmeric.reactivesample.repository;

import com.packt.ahmeric.reactivesample.model.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends R2dbcRepository<User, String> {
    Mono<User> findByEmail(String email);
}

