package com.cloudbeds.usermgm.rest.contract;

import com.cloudbeds.usermgm.model.request.AddUserAddressRequest;
import com.cloudbeds.usermgm.model.request.CreateUserRequest;
import com.cloudbeds.usermgm.model.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Tag(name = "User API", description = "Defines user management operations")
public interface UserContract {

    @PostMapping("/user")
    @Operation(description = "Create an user", summary = "Create user",
            responses = {
                    @ApiResponse(
                            description = "User created successfully",
                            responseCode = "201",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "Internal server error", responseCode = "500")
            },
            requestBody =
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "The request body for creating a new user",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateUserRequest.class))
            )
    )
    Mono<ResponseEntity<UserResponse>> createUser(@RequestBody @Valid @NonNull final CreateUserRequest user, ServerHttpRequest request);

    @PostMapping("/user/{userId}/address")
    @Operation(description = "Add an address to an existing user", summary = "Add address",
            responses = {
                    @ApiResponse(
                            description = "Address added successfully!",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))
                    ),
                    @ApiResponse(description = "Invalid parameters provided!", responseCode = "400"),
                    @ApiResponse(description = "Internal error", responseCode = "500")
            },
            requestBody =
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "The request body for adding a new address to an existing user",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AddUserAddressRequest.class))
            )
    )
    Mono<ResponseEntity<UserResponse>> addUserAddress(
            @Parameter(description = "Id of the user", required = true)
            @PathVariable(name = "userId") final Integer userId,
            @RequestBody @Valid @NonNull final AddUserAddressRequest address);


    @GetMapping("/users")
    @Operation(description = "Search users by country", summary = "Search users",
            responses = {
                    @ApiResponse(
                            description = "Search completed successfully!",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))
                    ),
                    @ApiResponse(description = "Invalid parameters provided!", responseCode = "400"),
                    @ApiResponse(description = "Internal error", responseCode = "500")
            })
    Flux<UserResponse> getUsersByCountry(
            @Parameter(description = "The country to search by", required = true)
            @RequestParam(value = "country") final String country
    );
}
