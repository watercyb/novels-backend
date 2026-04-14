package com.blueberryjoy.novel.exception;

import com.blueberryjoy.novel.exception.models.DuplicatedException;
import com.blueberryjoy.novel.exception.models.NotFoundException;
import com.blueberryjoy.novel.exception.models.UnauthorizedException;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
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

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handle(UnauthorizedException ex) {

        ErrorResponse response = new ErrorResponse(
                false,
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handle(BadRequestException ex) {

        ErrorResponse response = new ErrorResponse(
                false,
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}