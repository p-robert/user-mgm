package com.cloudbeds.usermgm.rest.contract;

import com.cloudbeds.usermgm.model.response.AddressResponse;
import com.cloudbeds.usermgm.model.request.CreateAddressRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Tag(name = "Address API", description = "Defines addresses management operations")
public interface AddressContract {


    @PostMapping("/address")
    @Operation(description = "Create an address", summary = "Create address",
            responses = {
                    @ApiResponse(
                            description = "Address created successfully!",
                            responseCode = "201",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AddressResponse.class))
                    ),
                    @ApiResponse(description = "Invalid parameters provided!", responseCode = "400"),
                    @ApiResponse(description = "Internal error", responseCode = "500")
            },
            requestBody =
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "The request body for creating a new address",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateAddressRequest.class))
            )
    )
    Mono<ResponseEntity<AddressResponse>> createAddress(@RequestBody @Valid @NonNull final CreateAddressRequest createAddressDTO, ServerHttpRequest request);
}
