package com.cryptoevent.exception;

public class RegistrationException extends Exception {

    public static final String FAIL_SEND_REGISTRATION = "FAIL_SEND_REGISTRATION";
    public static final String FAIL_SAVE_REGISTRATION = "FAIL_SAVE_REGISTRATION";

    public RegistrationException () {
    }

    public RegistrationException (String message) {
        super (message);
    }
}