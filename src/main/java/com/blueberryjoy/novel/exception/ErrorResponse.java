package com.blueberryjoy.novel.exception;

public record ErrorResponse(
        boolean success,
        String message
) {
}