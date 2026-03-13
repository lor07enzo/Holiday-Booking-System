package com.lorenzo.pelone.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Gestisce gli errori di validazione (quelli lanciati da @Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage()));
        
        log.warn("Validation failed: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    // Gestisce le eccezioni di business ("User not found" o dati errati)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Business logic error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    // Gestisce le eccezioni impreviste (500 Internal Server Error)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An internal error occurred. Please try again later.");
    }
}
