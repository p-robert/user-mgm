package com.cloudbeds.usermgm.errors;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(final String email) {
        super(String.format("User with associated email '%s' already exists!", email));
    }
}
