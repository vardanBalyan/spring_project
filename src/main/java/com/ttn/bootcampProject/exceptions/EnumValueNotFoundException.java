package com.ttn.bootcampProject.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EnumValueNotFoundException extends RuntimeException {
    public EnumValueNotFoundException(String message) {
        super(message);
    }
}
