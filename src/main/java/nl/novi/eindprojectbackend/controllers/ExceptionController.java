package nl.novi.eindprojectbackend.controllers;

import nl.novi.eindprojectbackend.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.dao.DataIntegrityViolationException;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionController {

    private static final Logger log = LoggerFactory.getLogger(ExceptionController.class);

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<Map<String, Object>> handleInternalServerException(InternalServerException ex, WebRequest request) {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
    }

    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<String> handleRecordNotFoundException(RecordNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(WebRequest request) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, "Cannot delete Repair Type because it is used in existing Repairs.", request);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequestException(BadRequestException ex, WebRequest request) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        return createErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), null);


    }

    @ExceptionHandler(ForbiddenActionException.class)
    public ResponseEntity<Map<String, Object>> handleForbiddenActionException(ForbiddenActionException ex, WebRequest request) {
        return createErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: ", ex);
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.put("error", "Internal Server Error");
        errorResponse.put("message", "An unexpected error occurred.");
        errorResponse.put("exceptionType", ex.getClass().getSimpleName());

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse(HttpStatus status, String message, WebRequest request) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", message);
        errorResponse.put("path", request.getDescription(false));
        errorResponse.put("exceptionType", status.name());

        log.error("Error response: {}", errorResponse);
        return new ResponseEntity<>(errorResponse, status);
    }
}
