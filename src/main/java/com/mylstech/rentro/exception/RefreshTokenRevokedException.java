package com.mylstech.rentro.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class RefreshTokenRevokedException extends RuntimeException {
    public RefreshTokenRevokedException(String refreshTokenWasRevoked) {
        super(refreshTokenWasRevoked);
    }
}
