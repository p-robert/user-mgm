package com.cloudbeds.usermgm.rest;

import com.cloudbeds.usermgm.model.response.AddressResponse;
import com.cloudbeds.usermgm.model.request.CreateAddressRequest;
import com.cloudbeds.usermgm.rest.contract.AddressContract;
import com.cloudbeds.usermgm.service.AddressService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class AddressController implements AddressContract {

    private final AddressService addressService;

    @Override
    public Mono<ResponseEntity<AddressResponse>> createAddress(@NonNull CreateAddressRequest createAddress, ServerHttpRequest request) {
        return addressService.createAddress(createAddress)
                .map(address -> ResponseEntity.created(buildUri(request, address)).body(address));
    }

    private URI buildUri(ServerHttpRequest request, AddressResponse address) {
        return UriComponentsBuilder
                .fromHttpRequest(request)
                .path("/{id}")
                .buildAndExpand(address.getAddressId())
                .toUri();
    }
}
