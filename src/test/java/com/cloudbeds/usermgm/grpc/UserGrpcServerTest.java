package com.cloudbeds.usermgm.grpc;

import com.cloudbeds.usermgm.generated.grpc.GetUserRequest;
import com.cloudbeds.usermgm.generated.grpc.GetUserResponse;
import com.cloudbeds.usermgm.generated.grpc.ReactorUserServiceGrpc;
import com.cloudbeds.usermgm.model.response.AddressResponse;
import com.cloudbeds.usermgm.model.response.UserResponse;
import com.cloudbeds.usermgm.service.UserGrpcService;
import com.cloudbeds.usermgm.service.UserService;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserGrpcServerTest {
    private ReactorUserServiceGrpc.ReactorUserServiceStub stub;
    @Mock
    private UserService userService;

    Server server;
    ManagedChannel managedChannel;

    @BeforeEach
    void beforeEach() throws IOException {
        var serverName = InProcessServerBuilder.generateName();

        server = InProcessServerBuilder.forName(serverName)
                .directExecutor()
                .addService(new UserGrpcService(userService))
                .build()
                .start();

        managedChannel = InProcessChannelBuilder.forName(serverName)
                .directExecutor()
                .build();
        stub = ReactorUserServiceGrpc.newReactorStub(managedChannel);
    }

    @AfterEach
    public void stopServer() throws InterruptedException {
        server.shutdown();
        server.awaitTermination();
        managedChannel.shutdown();

        server = null;
        managedChannel = null;
    }

    @Test
    public void retrieveUser_whenUserIdExist_shouldReturnExpected() {
        var userId = 1;
        var expected = UserResponse.builder()
                .firstName("Robert")
                .lastName("Puscasu")
                .email("robert_puscasu@yahoo.com")
                .addresses(List.of(AddressResponse.builder()
                        .addressId(12)
                        .address2("address 2")
                        .address1("address 1")
                        .country("romania")
                        .city("Bucuresti")
                        .state("Ilfov")
                        .zip("2143123")
                        .build()))
                .userId(userId)
                .build();

        when(userService.findUserById(any())).thenReturn(Mono.just(expected));
        Mono<GetUserRequest> req = Mono.just(GetUserRequest.newBuilder()
                .setUserId(userId)
                .build());
        Mono<GetUserResponse> resp = req.as(stub::retrieveUser);

        StepVerifier.create(resp)
                .assertNext(user -> {
                    assertThat(user).isNotNull();
                    assertThat(user.getAddresses().getAddress(0).getAddress1()).isEqualTo(expected.getAddresses().get(0).getAddress1());
                })
                .verifyComplete();
    }
}
