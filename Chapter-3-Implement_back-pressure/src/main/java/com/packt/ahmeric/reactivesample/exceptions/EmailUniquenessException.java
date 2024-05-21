package com.packt.ahmeric.reactivesample.exceptions;

public class EmailUniquenessException extends RuntimeException {
    public EmailUniquenessException(String message) {
        super(message);
    }
}
