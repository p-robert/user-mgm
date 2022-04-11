package com.cloudbeds.usermgm.errors.handler;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Application error", name = "ApplicationError")
public class ApplicationError {
    @Schema(description = "The error id")
    private String errorId;
    @Schema(description = "The error type")
    private String errorType;
    @Schema(description = "The http status")
    private Integer status;
    @Schema(description = "The error detail")
    private String errorDetail;
}
