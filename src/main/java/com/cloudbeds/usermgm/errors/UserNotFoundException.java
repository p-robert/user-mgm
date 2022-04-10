package com.cloudbeds.usermgm.errors;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(final Integer userId) {
        super(String.format("User Id '%d' not found!", userId));
    }
}
