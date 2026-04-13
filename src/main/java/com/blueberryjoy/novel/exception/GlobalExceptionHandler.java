package com.blueberryjoy.novel.exception;

import com.blueberryjoy.novel.exception.models.DuplicatedException;
import com.blueberryjoy.novel.exception.models.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handle(RuntimeException ex) {

        ErrorResponse response = new ErrorResponse(false, ex.getMessage());

        return ResponseEntity.status(400).body(response);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(NotFoundException ex) {

        ErrorResponse response = new ErrorResponse(
                false,
                ex.getMessage()
        );

        return ResponseEntity.status(404).body(response);
    }

    @ExceptionHandler(DuplicatedException.class)
    public ResponseEntity<ErrorResponse> handle(DuplicatedException ex) {

        ErrorResponse response = new ErrorResponse(
                false,
                ex.getMessage()
        );

        return ResponseEntity.status(409).body(response);
    }
}