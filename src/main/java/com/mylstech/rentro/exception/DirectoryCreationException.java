package com.mylstech.rentro.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DirectoryCreationException extends RuntimeException {
    public DirectoryCreationException(String couldNotInitializeStorageDirectories, IOException e) {
 super(couldNotInitializeStorageDirectories, e);
    }
}
