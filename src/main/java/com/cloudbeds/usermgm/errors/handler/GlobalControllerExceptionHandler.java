package com.cloudbeds.usermgm.errors.handler;

import com.cloudbeds.usermgm.errors.AddressNotFoundException;
import com.cloudbeds.usermgm.errors.DuplicateAddressException;
import com.cloudbeds.usermgm.errors.UserAlreadyExistsException;
import com.cloudbeds.usermgm.errors.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
@Slf4j
public class GlobalControllerExceptionHandler {

    @ExceptionHandler({UserAlreadyExistsException.class, DuplicateAddressException.class})
    @ResponseStatus(CONFLICT)
    @ResponseBody
    public ApplicationError handleDuplicates(final Exception exception, final ServerHttpRequest serverHttpRequest) {
        logError(exception, serverHttpRequest);
        return getApplicationError(exception, CONFLICT.value());
    }

    @ExceptionHandler({UserNotFoundException.class, AddressNotFoundException.class})
    @ResponseStatus(NOT_FOUND)
    @ResponseBody
    public ApplicationError handleNotFound(final Exception exception, final ServerHttpRequest serverHttpRequest) {
        logError(exception, serverHttpRequest);
        return getApplicationError(exception, NOT_FOUND.value());
    }

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public ApplicationError handleMethodArgumentNotValid(final WebExchangeBindException exception, final ServerHttpRequest serverHttpRequest) {
        logError(exception, serverHttpRequest);
        return getApplicationError(exception, BAD_REQUEST.value());
    }

    private static String fullUrl(final ServerHttpRequest serverHttpRequest) {
        return serverHttpRequest.toString();
    }

    private void logError(final Exception exception, final ServerHttpRequest serverHttpRequest) {
        var errorMessage = String.format("Rest error message: %s at path %s", exception.getMessage(), fullUrl(serverHttpRequest));
        log.error(errorMessage, exception);
    }

    private static ApplicationError getApplicationError(Exception e, Integer status) {
        return ApplicationError.builder()
                .errorType(e.getClass().getTypeName())
                .errorId(UUID.randomUUID().toString())
                .errorDetail(e.getMessage())
                .status(status)
                .build();
    }
}
