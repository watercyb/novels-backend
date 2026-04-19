package com.blueberryjoy.novel.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handle(ApiException ex) {
        ErrorResponse response = new ErrorResponse(false, ex.getMessage());
        System.out.println(ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(org.springframework.web.multipart.MultipartException.class)
    public ResponseEntity<ErrorResponse> handle(org.springframework.web.multipart.MultipartException ex) {
        ErrorResponse response = new ErrorResponse(false, "Invalid input");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(org.springframework.web.multipart.support.MissingServletRequestPartException.class)
    public ResponseEntity<ErrorResponse> handle(org.springframework.web.multipart.support.MissingServletRequestPartException ex) {
        ErrorResponse response = new ErrorResponse(false, "Invalid input");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(org.springframework.web.bind.MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handle(org.springframework.web.bind.MissingServletRequestParameterException ex) {
        ErrorResponse response = new ErrorResponse(false, "Invalid input");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handle(org.springframework.http.converter.HttpMessageNotReadableException ex) {
        ErrorResponse response = new ErrorResponse(false, "Invalid input");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}