package com.cloudbeds.usermgm.repository;

import com.cloudbeds.usermgm.BaseIntegrationTest;
import com.cloudbeds.usermgm.entity.AddressEntity;
import com.cloudbeds.usermgm.entity.UserAddressEntity;
import com.cloudbeds.usermgm.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;


public class UserRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserAddressRepository userAddressRepository;

    @BeforeEach
    void beforeEach() {
        userAddressRepository.deleteAll()
                .as(StepVerifier::create)
                .verifyComplete();
        addressRepository.deleteAll()
                .as(StepVerifier::create)
                .verifyComplete();
        userRepository.deleteAll()
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    public void findUserByEmail_whenExistingUser_shouldReturnExpected() {
        var userEntity = UserEntity.builder()
                .firstName("Robert")
                .lastName("puscasu")
                .password("password")
                .email("robert.puscasu@email.com")
                .build();
        userRepository.save(userEntity)
                .as(StepVerifier::create)
                .assertNext(savedUser -> {
                    assertThat(savedUser.getEmail()).isEqualTo(userEntity.getEmail());
                })
                .verifyComplete();

        userRepository.findUserByEmail(userEntity.getEmail())
                .as(StepVerifier::create)
                .assertNext(existingUser -> assertThat(existingUser.getEmail()).isEqualTo(userEntity.getEmail()))
                .verifyComplete();
    }

    @Test
    public void findUsersByCountry_whenMultipleUsers_shouldReturnExpected() {
        final UserEntity userEntity1 = UserEntity.builder()
                .lastName("test 1")
                .firstName("test 1")
                .email("test1@yahoo.com")
                .password("password 1")
                .build();

        final UserEntity userEntity2 = UserEntity.builder()
                .lastName("test 2")
                .firstName("test 2")
                .email("test2@yahoo.com")
                .password("password")
                .build();


        var savedUser1 = userRepository.save(userEntity1).block();
        var savedUser2 = userRepository.save(userEntity2).block();
        var address1 = AddressEntity.builder()
                .address1("address1")
                .country("Romania")
                .city("Bucharest")
                .state("Bucharest")
                .zip("41721")
                .build();

        var address2 = AddressEntity.builder()
                .address1("address 1")
                .address2("address 2")
                .country("Germany")
                .city("Berlin")
                .state("DE")
                .zip("90099")
                .build();

        AddressEntity savedAddress1 = addressRepository.save(address1).block();
        AddressEntity savedAddress2 = addressRepository.save(address2).block();

        userAddressRepository.save(UserAddressEntity.builder()
                .userId(savedUser1.getId())
                .addressId(savedAddress1.getAddressId())
                .build()).block();

        userAddressRepository.save(UserAddressEntity.builder()
                .userId(savedUser2.getId())
                .addressId(savedAddress2.getAddressId())
                .build()).block();

        userRepository.findUsersByCountry("Germany")
                .as(StepVerifier::create)
                .expectNextCount(1L)
                .assertNext(user -> {
                    assertThat(user.getEmail()).isEqualTo(savedUser2.getEmail());
                    assertThat(user.getFirstName()).isEqualTo(savedUser2.getFirstName());
                });
    }
}