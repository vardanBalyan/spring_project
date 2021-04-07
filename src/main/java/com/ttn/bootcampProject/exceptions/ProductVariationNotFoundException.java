package com.ttn.bootcampProject.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductVariationNotFoundException extends RuntimeException {
    public ProductVariationNotFoundException(String message) {
        super(message);
    }
}
