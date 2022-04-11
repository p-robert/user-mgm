package com.cloudbeds.usermgm.rest;

import com.cloudbeds.usermgm.BaseIntegrationTest;
import com.cloudbeds.usermgm.model.request.CreateAddressRequest;
import com.cloudbeds.usermgm.repository.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import reactor.test.StepVerifier;

import java.time.Duration;

public class AddressControllerIT extends BaseIntegrationTest {

    @Autowired
    private AddressRepository addressRepository;

    @BeforeEach
    void beforeEach() {
        webTestClient = webTestClient.mutate()
                .responseTimeout(Duration.ofSeconds(45))
                .build();
        addressRepository.deleteAll()
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    public void createAddress_whenValidInput_shouldReturnExpected() {
        var address =  CreateAddressRequest.builder()
                .address1("ha ha road")
                .address2("Bad route road")
                .city("Honolulu")
                .country("Hawaii")
                .state("no state")
                .zip("44421")
                .build();

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/address").build())
                .bodyValue(address)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .jsonPath("$.address1").isEqualTo(address.getAddress1())
                .jsonPath("$.address2").isEqualTo(address.getAddress2())
                .jsonPath("$.city").isEqualTo(address.getCity())
                .jsonPath("$.country").isEqualTo(address.getCountry())
                .jsonPath("$.state").isEqualTo(address.getState())
                .jsonPath("$.zip").isEqualTo(address.getZip());
    }

    @Test
    public void createAddress_whenAddressAlreadyExists_shouldReturnError() {
        var address =  CreateAddressRequest.builder()
                .address1("ha ha road")
                .address2("Bad route road")
                .city("Honolulu")
                .country("Hawaii")
                .state("no state")
                .zip("44421")
                .build();


        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/address").build())
                .bodyValue(address)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .jsonPath("$.address1").isEqualTo(address.getAddress1())
                .jsonPath("$.address2").isEqualTo(address.getAddress2())
                .jsonPath("$.city").isEqualTo(address.getCity())
                .jsonPath("$.country").isEqualTo(address.getCountry())
                .jsonPath("$.state").isEqualTo(address.getState())
                .jsonPath("$.zip").isEqualTo(address.getZip());


        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/address").build())
                .bodyValue(address)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.CONFLICT.value())
                .expectBody()
                .jsonPath("$.errorDetail").isEqualTo("An address already exists for this zip code: '44421'!");
    }
}
