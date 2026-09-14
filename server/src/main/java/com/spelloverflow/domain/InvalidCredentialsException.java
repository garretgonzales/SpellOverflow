package com.spelloverflow.domain;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Invalid username, email, or password.");
    }
}
