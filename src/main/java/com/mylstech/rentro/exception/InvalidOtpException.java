package com.mylstech.rentro.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidOtpException extends RuntimeException {
    public InvalidOtpException(String invalidOtpOrOtpExpired) {
        super(invalidOtpOrOtpExpired);
    }
}
