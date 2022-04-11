package com.cloudbeds.usermgm.service;

import com.cloudbeds.usermgm.errors.UserNotFoundException;
import com.cloudbeds.usermgm.generated.grpc.Address;
import com.cloudbeds.usermgm.generated.grpc.AddressList;
import com.cloudbeds.usermgm.generated.grpc.GetUserRequest;
import com.cloudbeds.usermgm.generated.grpc.GetUserResponse;
import com.cloudbeds.usermgm.generated.grpc.ReactorUserServiceGrpc;
import com.cloudbeds.usermgm.model.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserGrpcService extends ReactorUserServiceGrpc.UserServiceImplBase {

    private final UserService userService;

    @Override
    public Mono<GetUserResponse> retrieveUser(Mono<GetUserRequest> request) {
        return request
                .map(GetUserRequest::getUserId)
                .flatMap(userService::findUserById)
                .map(this::buildResponse);

    }

    private GetUserResponse buildResponse(UserResponse user) {
        return GetUserResponse
                .newBuilder()
                .setLastName(user.getLastName())
                .setFirstName(user.getFirstName())
                .setUserId(user.getUserId())
                .setEmail(user.getEmail())
                .setAddresses(addAddresses(user))
                .build();
    }

    private AddressList addAddresses(UserResponse user) {
        return AddressList.newBuilder()
                .addAllAddress(user.getAddresses().stream()
                        .map(address -> Address.newBuilder()
                                .setAddress1(address.getAddress1())
                                .setAddress2(address.getAddress2())
                                .setCity(address.getCity())
                                .setCountry(address.getCountry())
                                .setState(address.getState())
                                .build()).collect(Collectors.toList()))
                .build();
    }
}
