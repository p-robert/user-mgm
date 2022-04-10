package com.cloudbeds.usermgm.errors;

public class DuplicateAddressException extends RuntimeException {
    public DuplicateAddressException(final String zip) {
        super(String.format("An address already exists for this zip code: '%s'!", zip));
    }
}
