package com.cloudbeds.usermgm.repository;

import com.cloudbeds.usermgm.BaseIntegrationTest;
import com.cloudbeds.usermgm.entity.AddressEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

public class AddressRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private AddressRepository addressRepository;

    @BeforeEach
    void beforeEach() {
        addressRepository.deleteAll()
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    public void findAddressByZip_whenExistingZip_shouldReturnExpected() {
        var address = AddressEntity.builder()
                .address1("New York Street")
                .zip("98231")
                .state("New York")
                .city("New York")
                .country("USA")
                .build();

        StepVerifier.create(addressRepository.save(address))
                .assertNext(anAddress -> assertThat(anAddress.getZip()).isEqualTo(address.getZip()))
                .verifyComplete();
    }
}
