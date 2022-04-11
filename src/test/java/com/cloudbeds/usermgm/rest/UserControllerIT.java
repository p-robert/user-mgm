package com.cloudbeds.usermgm.rest;

import com.cloudbeds.usermgm.BaseIntegrationTest;
import com.cloudbeds.usermgm.model.request.AddUserAddressRequest;
import com.cloudbeds.usermgm.model.request.CreateAddressRequest;
import com.cloudbeds.usermgm.model.request.CreateUserRequest;
import com.cloudbeds.usermgm.model.response.AddressResponse;
import com.cloudbeds.usermgm.model.response.UserResponse;
import com.cloudbeds.usermgm.repository.AddressRepository;
import com.cloudbeds.usermgm.repository.UserAddressRepository;
import com.cloudbeds.usermgm.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserControllerIT extends BaseIntegrationTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserAddressRepository userAddressRepository;

    @BeforeEach
    void beforeEach() {
        webTestClient = webTestClient.mutate()
                .responseTimeout(Duration.ofSeconds(45))
                .build();
        userAddressRepository.deleteAll()
                .as(StepVerifier::create)
                .verifyComplete();
        userRepository.deleteAll()
                .as(StepVerifier::create)
                .verifyComplete();
        addressRepository.deleteAll()
                .as(StepVerifier::create)
                .verifyComplete();
    }


    @Test
    void createUser_whenValidInput_shouldReturnExpected() {
        var user = CreateUserRequest.builder()
                .firstName("Robert")
                .lastName("Puscasu")
                .password("avalidpassword")
                .email("puscasu.robert@gmail.com")
                .build();

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/user").build())
                .bodyValue(user)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .jsonPath("$.lastName").isEqualTo("Puscasu")
                .jsonPath("$.firstName").isEqualTo("Robert")
                .jsonPath("$.email").isEqualTo("puscasu.robert@gmail.com");
    }

    @Test
    public void createUser_whenEmailAlreadyRegistered_shouldReturnError() {
        var user = CreateUserRequest.builder()
                .firstName("Robert")
                .lastName("Puscasu")
                .password("avalidpassword")
                .email("puscasu.robert@gmail.com")
                .build();

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/user").build())
                .bodyValue(user)
                .exchange()
                .expectStatus()
                .isCreated();

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/user").build())
                .bodyValue(user)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.CONFLICT.value());

    }

    @Test
    public void createUser_whenInvalidEmail_shouldReturnError() {
        var user = CreateUserRequest.builder()
                .firstName("Robert")
                .lastName("Puscasu")
                .password("test")
                .email("puscasu.rober")
                .build();

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/user").build())
                .bodyValue(user)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void addUserAddress_whenValidInput_shouldReturnExpected() {
        var user = CreateUserRequest.builder()
                .firstName("Robert")
                .lastName("Puscasu")
                .password("avalidpassword")
                .email("puscasu.robert@gmail.com")
                .build();

        UserResponse userResponse = createUser(user);

        var address = CreateAddressRequest.builder()
                .address1("ha ha road")
                .address2("Bad route road")
                .city("Honolulu")
                .country("Hawaii")
                .state("no state")
                .zip("44421")
                .build();

        AddressResponse addressResponse = createAddress(address);

        AddUserAddressRequest addUserAddressRequest = AddUserAddressRequest.builder()
                .addressId(addressResponse.getAddressId())
                .build();

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/user")
                        .pathSegment(String.valueOf(userResponse.getUserId()))
                        .pathSegment("address").build()
                )
                .bodyValue(addUserAddressRequest)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.lastName").isEqualTo(userResponse.getLastName())
                .jsonPath("$.firstName").isEqualTo(user.getFirstName())
                .jsonPath("$.email").isEqualTo(user.getEmail())
                .jsonPath("$.addresses[0].addressId").isEqualTo(addressResponse.getAddressId())
                .jsonPath("$.addresses[0].address1").isEqualTo(addressResponse.getAddress1())
                .jsonPath("$.addresses[0].address2").isEqualTo(addressResponse.getAddress2())
                .jsonPath("$.addresses[0].city").isEqualTo(addressResponse.getCity())
                .jsonPath("$.addresses[0].country").isEqualTo(addressResponse.getCountry())
                .jsonPath("$.addresses[0].state").isEqualTo(addressResponse.getState())
                .jsonPath("$.addresses[0].zip").isEqualTo(addressResponse.getZip());
    }

    @Test
    void addUserAddress_whenAddressDoesNotExist_shouldReturnError() {
        var user = CreateUserRequest.builder()
                .firstName("Robert")
                .lastName("Puscasu")
                .password("avalidpassword")
                .email("puscasu.robert@gmail.com")
                .build();

        UserResponse userResponse = createUser(user);

        AddUserAddressRequest addUserAddressRequest = AddUserAddressRequest.builder()
                .addressId(Integer.MAX_VALUE)
                .build();

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/user")
                        .pathSegment(String.valueOf(userResponse.getUserId()))
                        .pathSegment("address").build()
                )
                .bodyValue(addUserAddressRequest)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    public void addAddresses_whenMultipleAddressesToSameUser_shouldReturnExpected() {
        var user = CreateUserRequest.builder()
                .firstName("First")
                .lastName("User")
                .password("avalidpassword")
                .email("first.user@gmail.com")
                .build();

        var firstAddress = CreateAddressRequest.builder()
                .address1("ha ha road")
                .address2("Bad route road")
                .city("Honolulu")
                .country("Hawaii")
                .state("no state")
                .zip("44421")
                .build();
        var secondAddress = CreateAddressRequest.builder()
                .address1("Silk Road")
                .address2("Pan Americana")
                .city("Bucharest")
                .country("Romania")
                .state("Ilfov")
                .zip("44291")
                .build();

        UserResponse createdUser = createUser(user);
        AddressResponse createdFirstAddress = createAddress(firstAddress);
        AddressResponse createdSecondAddress = createAddress(secondAddress);

        addAddressToUser(createdUser, createdFirstAddress);

        AddUserAddressRequest addSecondUserAddressRequest = AddUserAddressRequest.builder()
                .addressId(createdSecondAddress.getAddressId())
                .build();

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/user")
                        .pathSegment(String.valueOf(createdUser.getUserId()))
                        .pathSegment("address").build()
                )
                .bodyValue(addSecondUserAddressRequest)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.lastName").isEqualTo(user.getLastName())
                .jsonPath("$.firstName").isEqualTo(user.getFirstName())
                .jsonPath("$.email").isEqualTo(user.getEmail())
                .jsonPath("$.addresses[0].addressId").isEqualTo(createdFirstAddress.getAddressId())
                .jsonPath("$.addresses[0].address1").isEqualTo(createdFirstAddress.getAddress1())
                .jsonPath("$.addresses[0].address2").isEqualTo(createdFirstAddress.getAddress2())
                .jsonPath("$.addresses[0].city").isEqualTo(createdFirstAddress.getCity())
                .jsonPath("$.addresses[0].country").isEqualTo(createdFirstAddress.getCountry())
                .jsonPath("$.addresses[0].state").isEqualTo(createdFirstAddress.getState())
                .jsonPath("$.addresses[0].zip").isEqualTo(createdFirstAddress.getZip())
                .jsonPath("$.addresses[1].addressId").isEqualTo(createdSecondAddress.getAddressId())
                .jsonPath("$.addresses[1].address1").isEqualTo(createdSecondAddress.getAddress1())
                .jsonPath("$.addresses[1].address2").isEqualTo(createdSecondAddress.getAddress2())
                .jsonPath("$.addresses[1].city").isEqualTo(createdSecondAddress.getCity())
                .jsonPath("$.addresses[1].country").isEqualTo(createdSecondAddress.getCountry())
                .jsonPath("$.addresses[1].state").isEqualTo(createdSecondAddress.getState())
                .jsonPath("$.addresses[1].zip").isEqualTo(createdSecondAddress.getZip());

    }


    @Test
    public void getUsers_whenSearchByCountry_shouldReturnExpected() {
        var user = CreateUserRequest.builder()
                .firstName("English")
                .lastName("User")
                .password("avalidpassword")
                .email("english.user@gmail.com")
                .build();

        var englishAddress = CreateAddressRequest.builder()
                .address1("UK Street 1")
                .address2("UK Street 2")
                .city("Honolulu")
                .country("Great Britain")
                .state("GB")
                .zip("44421")
                .build();

        UserResponse createdEnglishUser = createUser(user);
        AddressResponse createdEnglishAddress = createAddress(englishAddress);
        UserResponse createdEnglishUserWithAddress = addAddressToUser(createdEnglishUser, createdEnglishAddress);


        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/users").queryParam("country", "Great Britain").build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(UserResponse.class)
                .consumeWith(response -> {
                    assertThat(response).isNotNull();
                    List<UserResponse> users = response.getResponseBody();
                    assertThat(users).isNotNull();
                    assertThat(users).isNotEmpty();
                    assertThat(users.get(0).getLastName()).isEqualTo(createdEnglishUserWithAddress.getLastName());
                    assertThat(users.get(0).getFirstName()).isEqualTo(createdEnglishUserWithAddress.getFirstName());
                    assertThat(users.get(0).getEmail()).isEqualTo(createdEnglishUserWithAddress.getEmail());
                    assertThat(users.get(0).getAddresses().get(0).getAddressId()).isEqualTo(createdEnglishAddress.getAddressId());
                    assertThat(users.get(0).getAddresses().get(0).getAddress1()).isEqualTo(createdEnglishAddress.getAddress1());
                    assertThat(users.get(0).getAddresses().get(0).getAddress2()).isEqualTo(createdEnglishAddress.getAddress2());
                    assertThat(users.get(0).getAddresses().get(0).getCountry()).isEqualTo(createdEnglishAddress.getCountry());
                    assertThat(users.get(0).getAddresses().get(0).getZip()).isEqualTo(createdEnglishAddress.getZip());
                    assertThat(users.get(0).getAddresses().get(0).getState()).isEqualTo(createdEnglishAddress.getState());
                });
    }
}
