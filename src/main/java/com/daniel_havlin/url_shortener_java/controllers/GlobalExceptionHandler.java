package com.daniel_havlin.url_shortener_java.controllers;

import com.daniel_havlin.url_shortener_java.exceptions.ErrorResponse;
import com.daniel_havlin.url_shortener_java.exceptions.InvalidUrlException;
import com.daniel_havlin.url_shortener_java.exceptions.NonfunctioningUrlException;
import com.daniel_havlin.url_shortener_java.exceptions.NotSafeUrlException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidUrlException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUrl(InvalidUrlException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), 422);
        return ResponseEntity.unprocessableContent().body(errorResponse);
    }

    @ExceptionHandler(NonfunctioningUrlException.class)
    public ResponseEntity<ErrorResponse> handleNonFunctioningUrl(NonfunctioningUrlException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), 422);
        return ResponseEntity.unprocessableContent().body(errorResponse);
    }

    @ExceptionHandler(NotSafeUrlException.class)
    public ResponseEntity<ErrorResponse> handleNotSafeUrl(NotSafeUrlException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), 422);
        return ResponseEntity.unprocessableContent().body(errorResponse);
    }
}
