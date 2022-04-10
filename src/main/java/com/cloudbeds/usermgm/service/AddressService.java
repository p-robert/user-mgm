package com.cloudbeds.usermgm.service;

import com.cloudbeds.usermgm.entity.AddressEntity;
import com.cloudbeds.usermgm.errors.DuplicateAddressException;
import com.cloudbeds.usermgm.mapper.AddressMapper;
import com.cloudbeds.usermgm.model.request.CreateAddressRequest;
import com.cloudbeds.usermgm.model.response.AddressResponse;
import com.cloudbeds.usermgm.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import static reactor.core.publisher.Mono.defer;

@Service
@Transactional
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;


    public Mono<AddressResponse> createAddress(final CreateAddressRequest createAddressRequest) {
        return checkIfAddressAlreadyExists(createAddressRequest).switchIfEmpty(defer(() -> createNewAddress(createAddressRequest)));
    }

    private Mono<AddressResponse> createNewAddress(CreateAddressRequest createAddressRequest) {
        return Mono.just(createAddressRequest)
                .map(addressMapper::toEntity)
                .flatMap(addressRepository::save)
                .map(addressMapper::toResponse);
    }

    private Mono<AddressResponse> checkIfAddressAlreadyExists(CreateAddressRequest createAddressRequest) {
        return Mono.just(createAddressRequest)
                .map(CreateAddressRequest::getZip)
                .flatMap(addressRepository::findAddressByZip)
                .map(AddressEntity::getZip)
                .map(DuplicateAddressException::new)
                .flatMap(Mono::error);
    }
}
