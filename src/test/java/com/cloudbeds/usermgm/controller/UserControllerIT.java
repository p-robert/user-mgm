package com.cloudbeds.usermgm.controller;

import com.cloudbeds.usermgm.BaseIntegrationTest;
import com.cloudbeds.usermgm.entity.UserEntity;
import com.cloudbeds.usermgm.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

public class UserControllerIT extends BaseIntegrationTest {
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void beforeEach() {
        var user = UserEntity.builder()
                .firstName("Robert")
                .lastName("Puscasu")
                .password("test1234")
                .email("puscasu.robert@gmail.com")
                .build();
        userRepository.save(user)
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll()
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void createUser_whenValidInput_shouldReturnExpected() {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/user").build())
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .jsonPath("$.lastName").isEqualTo("Puscasu")
                .jsonPath("$.firstName").isEqualTo("Robert");
    }
}
