package com.priyanshparekh.fairshareapi.handler.exception;

public class AccountCreatedButLoginFailedException extends RuntimeException {
    public AccountCreatedButLoginFailedException(String message) {
        super(message);
    }
}
