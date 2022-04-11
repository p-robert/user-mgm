package com.cloudbeds.usermgm;

import com.cloudbeds.usermgm.config.FlywayConfig;
import com.cloudbeds.usermgm.model.request.AddUserAddressRequest;
import com.cloudbeds.usermgm.model.request.CreateAddressRequest;
import com.cloudbeds.usermgm.model.request.CreateUserRequest;
import com.cloudbeds.usermgm.model.response.AddressResponse;
import com.cloudbeds.usermgm.model.response.UserResponse;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.PostgreSQLR2DBCDatabaseContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@AutoConfigureWebTestClient
@SpringBootTest
@Testcontainers
@ActiveProfiles("test-containers-flyway")
@Import({FlywayConfig.class, FlywayAutoConfiguration.class})
public class BaseIntegrationTest {
    @Autowired protected WebTestClient webTestClient;

    private static final PostgreSQLContainer<?> postgresql;
    private static final PostgreSQLR2DBCDatabaseContainer reactivePostgresql;

    static {
        postgresql = new PostgreSQLContainer<>(DockerImageName.parse("postgres:14.2"))
                .withDatabaseName("user_mgm")
                .withPassword("test")
                .withUsername("test")
                .withReuse(false);
        postgresql.setWaitStrategy(Wait.defaultWaitStrategy().withStartupTimeout(Duration.of(60, ChronoUnit.SECONDS)));
        reactivePostgresql = new PostgreSQLR2DBCDatabaseContainer(postgresql);
        reactivePostgresql.start();
    }

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        ConnectionFactoryOptions connectionFactoryOptions = PostgreSQLR2DBCDatabaseContainer.getOptions(postgresql);

        registry.add("spring.r2dbc.url", () -> r2dbcUrl(connectionFactoryOptions));
        registry.add("spring.r2dbc.username", () -> connectionFactoryOptions.getValue(ConnectionFactoryOptions.USER));
        registry.add("spring.r2dbc.password", () -> connectionFactoryOptions.getValue(ConnectionFactoryOptions.PASSWORD));
        registry.add("spring.flyway.url", postgresql::getJdbcUrl);
        registry.add("spring.flyway.enabled", () -> true);
        registry.add("spring.flyway.user", () -> connectionFactoryOptions.getValue(ConnectionFactoryOptions.USER));
        registry.add("spring.flyway.password", () -> connectionFactoryOptions.getValue(ConnectionFactoryOptions.PASSWORD));
        registry.add("logging.level.org.flywaydb", () -> "debug");
    }

    private static String r2dbcUrl(ConnectionFactoryOptions connectionFactoryOptions) {
        return String.format("r2dbc:postgresql://%s:%s/%s",
                connectionFactoryOptions.getValue(ConnectionFactoryOptions.HOST),
                connectionFactoryOptions.getValue(ConnectionFactoryOptions.PORT),
                connectionFactoryOptions.getValue(ConnectionFactoryOptions.DATABASE)
        );
    }

    protected UserResponse addAddressToUser(UserResponse user, AddressResponse address) {
        AddUserAddressRequest addFirstAddressRequest = AddUserAddressRequest.builder()
                .addressId(address.getAddressId())
                .build();

        FluxExchangeResult<UserResponse> addressResult = webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/user")
                        .pathSegment(String.valueOf(user.getUserId()))
                        .pathSegment("address").build()
                )
                .bodyValue(addFirstAddressRequest)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(UserResponse.class);

        return addressResult.getResponseBody().blockFirst();

    }

    protected UserResponse createUser(CreateUserRequest user) {
        FluxExchangeResult<UserResponse> userResult = webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/user").build())
                .bodyValue(user)
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(UserResponse.class);

        return userResult.getResponseBody().blockFirst();
    }

    protected AddressResponse createAddress(CreateAddressRequest address) {
        FluxExchangeResult<AddressResponse> addressResult = webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/address").build())
                .bodyValue(address)
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(AddressResponse.class);

        AddressResponse addressResponse = addressResult.getResponseBody().blockFirst();
        return addressResponse;
    }

}
