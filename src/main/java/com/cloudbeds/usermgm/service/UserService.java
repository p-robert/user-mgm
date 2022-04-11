package com.cloudbeds.usermgm.service;

import com.cloudbeds.usermgm.entity.AddressEntity;
import com.cloudbeds.usermgm.entity.UserAddressEntity;
import com.cloudbeds.usermgm.errors.AddressNotFoundException;
import com.cloudbeds.usermgm.errors.UserAlreadyExistsException;
import com.cloudbeds.usermgm.errors.UserNotFoundException;
import com.cloudbeds.usermgm.mapper.AddressMapper;
import com.cloudbeds.usermgm.mapper.UserMapper;
import com.cloudbeds.usermgm.model.request.AddUserAddressRequest;
import com.cloudbeds.usermgm.model.request.CreateUserRequest;
import com.cloudbeds.usermgm.model.response.AddressResponse;
import com.cloudbeds.usermgm.model.response.UserResponse;
import com.cloudbeds.usermgm.repository.AddressRepository;
import com.cloudbeds.usermgm.repository.UserAddressRepository;
import com.cloudbeds.usermgm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final UserAddressRepository userAddressRepository;
    private final UserMapper userMapper;
    private final AddressMapper addressMapper;

    public Mono<UserResponse> createUser(CreateUserRequest createUserRequest) {
        return userRepository.findUserByEmail(createUserRequest.getEmail())
                .flatMap(existingUser -> Mono.error(new UserAlreadyExistsException(existingUser.getEmail())))
                .map(UserResponse.class::cast)
                .switchIfEmpty(Mono.defer(() -> Mono.just(createUserRequest)
                .map(userMapper::toEntity)
                .flatMap(userRepository::save)
                .map(userMapper::toResponse)
                .flatMap(this::addUserAddresses)));
    }

    public Flux<UserResponse> getUsersByCountry(String country) {
        return userRepository.findUsersByCountry(country)
                .map(userMapper::toResponse)
                .flatMap(this::addUserAddresses);
    }

    public Mono<UserResponse> findUserById(Integer userId) {
        return userRepository.findById(userId)
                .map(userMapper::toResponse)
                .flatMap(this::addUserAddresses);
    }

    public Mono<UserResponse> addUserAddress(Integer userId, AddUserAddressRequest address) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new UserNotFoundException(userId))))
                .map(ignore -> address)
                .map(AddUserAddressRequest::getAddressId)
                .flatMap(addressRepository::findById)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new AddressNotFoundException(address.getAddressId()))))
                .map(AddressEntity::getAddressId)
                .map(addressId -> UserAddressEntity.builder().addressId(addressId).userId(userId).build())
                .flatMap(userAddressRepository::save)
                .map(UserAddressEntity::getUserId)
                .flatMap(userRepository::findById)
                .map(userMapper::toResponse)
                .flatMap(this::addUserAddresses);
    }

    private Mono<List<AddressResponse>> getUserAddresses(UserResponse user) {
        return Mono.just(user)
                .map(UserResponse::getUserId)
                .flatMapMany(userAddressRepository::findAllByUserId)
                .map(UserAddressEntity::getAddressId)
                .collectList()
                .flatMapMany(addressRepository::findAllByAddressIdIn)
                .map(addressMapper::toResponse)
                .collectList();
    }

    private Mono<UserResponse> addUserAddresses(UserResponse user) {
        return Mono.zip(
                        Mono.just(user),
                        getUserAddresses(user)
                )
                .map(this::toResponse);
    }

    private UserResponse toResponse(Tuple2<UserResponse, List<AddressResponse>> userAndAddresses) {
        return userAndAddresses.getT1().withAddresses(userAndAddresses.getT2());
    }
}

