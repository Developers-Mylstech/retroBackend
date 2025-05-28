package com.mylstech.rentro.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class EmailSendingException extends RuntimeException {
    public EmailSendingException(String failedToSendEmail, Exception e) {
        super(failedToSendEmail, e);
    }
}
