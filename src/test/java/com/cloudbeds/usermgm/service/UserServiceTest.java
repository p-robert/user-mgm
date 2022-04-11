package com.cloudbeds.usermgm.service;

import com.cloudbeds.usermgm.entity.UserEntity;
import com.cloudbeds.usermgm.mapper.UserMapper;
import com.cloudbeds.usermgm.model.request.AddUserAddressRequest;
import com.cloudbeds.usermgm.model.request.CreateUserRequest;
import com.cloudbeds.usermgm.repository.AddressRepository;
import com.cloudbeds.usermgm.repository.UserAddressRepository;
import com.cloudbeds.usermgm.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserAddressRepository userAddressRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    public void addAddress_whenUserDoesNotExist_shouldReturnError() {
        when(userRepository.findById(anyInt())).thenReturn(Mono.empty());

        userService.addUserAddress(12, AddUserAddressRequest.builder().build())
                .as(StepVerifier::create)
                .expectErrorMatches(ex -> ex.getMessage().equals("User Id '12' not found!"))
                .verify();
    }

    @Test
    public void createUser_whenEmailAlreadyRegistered_shouldReturnError() {
        final String existingEmail = "existing_email@yahoo.com";

        when(userRepository.findUserByEmail(existingEmail))
                .thenReturn(Mono.just(UserEntity.builder()
                        .email(existingEmail)
                        .build()));

        userService.createUser(CreateUserRequest.builder().email(existingEmail).build())
                .as(StepVerifier::create)
                .expectErrorMatches(ex -> ex.getMessage().equals("User with associated email 'existing_email@yahoo.com' already exists!"))
                .verify();
    }
}
