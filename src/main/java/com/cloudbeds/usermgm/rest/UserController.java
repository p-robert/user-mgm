package com.cloudbeds.usermgm.rest;


import com.cloudbeds.usermgm.model.request.AddUserAddressRequest;
import com.cloudbeds.usermgm.model.request.CreateUserRequest;
import com.cloudbeds.usermgm.model.response.UserResponse;
import com.cloudbeds.usermgm.rest.contract.UserContract;
import com.cloudbeds.usermgm.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class UserController implements UserContract {

    private final UserService userService;

    @Override
    public Mono<ResponseEntity<UserResponse>> createUser(@NonNull CreateUserRequest createUserRequest, ServerHttpRequest request) {
        return userService.createUser(createUserRequest)
                .map(user -> ResponseEntity
                        .created(buildUri(request, user))
                        .body(user)
                );
    }

    private URI buildUri(ServerHttpRequest request, UserResponse user) {
        return UriComponentsBuilder
                .fromHttpRequest(request)
                .path("/{id}")
                .buildAndExpand(user.getUserId())
                .toUri();
    }

    @Override
    public Mono<ResponseEntity<UserResponse>> addUserAddress(Integer userId, @NonNull AddUserAddressRequest address) {
        return userService.addUserAddress(userId, address).map(ResponseEntity::ok);
    }

    @Override
    public Flux<UserResponse> getUsersByCountry(String country) {
        return userService.getUsersByCountry(country);
    }
}
