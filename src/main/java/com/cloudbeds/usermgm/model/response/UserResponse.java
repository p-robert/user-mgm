package com.cloudbeds.usermgm.model.response;


import com.cloudbeds.usermgm.model.response.AddressResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.With;

import java.util.List;

@Data
@Builder
@Schema(description = "A response containing an user", name = "UserResponseDTO")
@With
public class UserResponse {

    @Schema(description = "The user Id")
    private Integer userId;

    @Schema(description = "The user last name")
    private String lastName;

    @Schema(description = "The user first name")
    private String firstName;

    @Schema(description = "The user email")
    private String email;

    @Schema(description = "The list of user addresses")
    private List<AddressResponse> addresses;
}