package com.blueberryjoy.novel.exception;

import com.blueberryjoy.novel.exception.models.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handle(ApiException ex) {
        ErrorResponse response = new ErrorResponse(false, ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(response);
    }
}