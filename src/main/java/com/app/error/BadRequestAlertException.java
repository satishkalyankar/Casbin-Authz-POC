package com.app.error;

public class BadRequestAlertException extends RuntimeException {

    public BadRequestAlertException(String message ) {
        super(message);
    }

}
