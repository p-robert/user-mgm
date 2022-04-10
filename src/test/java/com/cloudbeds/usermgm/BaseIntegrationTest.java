package com.cloudbeds.usermgm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

@AutoConfigureWebTestClient
@SpringBootTest
@Testcontainers
@ActiveProfiles("test-containers-flyway")
public class BaseIntegrationTest {
    @Autowired protected WebTestClient webTestClient;
}
