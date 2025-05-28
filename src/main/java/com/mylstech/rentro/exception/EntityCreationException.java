package com.mylstech.rentro.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EntityCreationException extends RuntimeException {
    public EntityCreationException(String failedToCreateBrand, Exception e) {
        super(failedToCreateBrand, e);
    }
}
