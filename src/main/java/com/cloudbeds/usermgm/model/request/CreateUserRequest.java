package com.cloudbeds.usermgm.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Value
@Builder
@AllArgsConstructor
@Schema(description = "Create user request", name = "CreateUserRequest")
public class CreateUserRequest {

    @Schema(description = "User first name", required = true)
    @NotNull(message = "{user.firstname.mandatory}")
    String firstName;

    @Schema(description = "User last name", required = true)
    @NotNull(message = "{user.lastname.mandatory}")
    String lastName;

    @Schema(description = "User email", required = true)
    @NotNull(message = "{user.email.mandatory}")
    @Email(message = "{user.email.invalid}")
    String email;

    @Schema(description = "User password", required = true, minLength = 8)
    @NotNull(message = "{user.password.mandatory}")
    @Size(min = 8, message = "{user.password.min}")
    String password;
}