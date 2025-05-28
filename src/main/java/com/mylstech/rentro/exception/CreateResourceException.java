package com.mylstech.rentro.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CreateResourceException extends RuntimeException {
    public CreateResourceException(String message, Exception cause) {
        super(message, cause);
    }
}
