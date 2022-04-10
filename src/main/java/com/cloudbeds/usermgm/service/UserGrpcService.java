package com.cloudbeds.usermgm.service;

import com.cloudbeds.usermgm.generated.grpc.GetUserRequest;
import com.cloudbeds.usermgm.generated.grpc.GetUserResponse;
import com.cloudbeds.usermgm.generated.grpc.ReactorUserServiceGrpc;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserGrpcService extends ReactorUserServiceGrpc.UserServiceImplBase {

    private final UserService userService;

    @Override
    public Mono<GetUserResponse> retrieveUser(Mono<GetUserRequest> request) {
        return request
                .map(GetUserRequest::getUserId)
                .flatMap(userService::findUserById)
                .map(user -> GetUserResponse.newBuilder()
                        .setUserId(user.getUserId())
                        .setEmail(user.getEmail()).build());

    }
}
