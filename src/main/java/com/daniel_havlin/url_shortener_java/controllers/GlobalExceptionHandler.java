package com.daniel_havlin.url_shortener_java.controllers;

import com.daniel_havlin.url_shortener_java.exceptions.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleIncorrectUserInput(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream().map((FieldError::getField)).toList();
        ErrorResponse errorResponse = new ErrorResponse(
                errors.toString(),
                "",
                422);
        return ResponseEntity.unprocessableContent().body(errorResponse);
    }

    @ExceptionHandler(InvalidUrlException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUrl(InvalidUrlException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                "You've given an invalid url, try it in your browser and correct any mistakes.",
                422);
        return ResponseEntity.unprocessableContent().body(errorResponse);
    }

    @ExceptionHandler(NonfunctioningUrlException.class)
    public ResponseEntity<ErrorResponse> handleNonFunctioningUrl(NonfunctioningUrlException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                "Address given cannot be reached, are you sure it's a functioning address?",
                422);
        return ResponseEntity.unprocessableContent().body(errorResponse);
    }

    @ExceptionHandler(NotSafeUrlException.class)
    public ResponseEntity<ErrorResponse> handleNotSafeUrl(NotSafeUrlException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                "Woah buddy, we don't want anything dangerous here, put the URL away.",
                422);
        return ResponseEntity.unprocessableContent().body(errorResponse);
    }

    @ExceptionHandler(UrlTakenException.class)
    public ResponseEntity<ErrorResponse> handleTakenUrl(UrlTakenException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                "We've already shortened that url, you can check it out at http://localhost/" + ex.getAssociatedShortCode(),
                409);
        return ResponseEntity.status(409).body(errorResponse);
    }

    @ExceptionHandler(FailedToCreateUrlException.class)
    public ResponseEntity<ErrorResponse> handleUrlCreationFailure(FailedToCreateUrlException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(),
                "Hmmm, something went wrong on our end.",
                500);
        return ResponseEntity.status(500).body(errorResponse);
    }
}
