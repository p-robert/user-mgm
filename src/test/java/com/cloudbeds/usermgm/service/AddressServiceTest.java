package com.cloudbeds.usermgm.service;

import com.cloudbeds.usermgm.entity.AddressEntity;
import com.cloudbeds.usermgm.model.request.CreateAddressRequest;
import com.cloudbeds.usermgm.repository.AddressRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {
    @InjectMocks
    private AddressService addressService;

    @Mock
    private AddressRepository addressRepository;

    @Test
    void createAddress_whenZipAlreadyExists_shouldReturnError() {
        when(addressRepository.findAddressByZip(anyString()))
                .thenReturn(Mono.just(AddressEntity.builder().zip("1234").build()));

        addressService.createAddress(CreateAddressRequest.builder().zip("1234").build())
                .as(StepVerifier::create)
                .expectErrorMatches(ex -> ex.getMessage().equals("An address already exists for this zip code: '1234'!"))
                .verify();
    }
}
