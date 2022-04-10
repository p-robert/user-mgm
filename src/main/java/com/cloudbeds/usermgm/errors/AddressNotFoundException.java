package com.cloudbeds.usermgm.errors;

public class AddressNotFoundException extends RuntimeException {
    public AddressNotFoundException(final Integer addressId) {
        super(String.format("Address with id '%d' not found!", addressId));
    }
}
