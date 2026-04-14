package com.blueberryjoy.novel.exception.models;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}