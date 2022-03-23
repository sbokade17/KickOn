package com.dooffle.KickOn.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(CustomAppException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomAppException pe) {

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError(pe.getError());
        errorResponse.setMessage(pe.getMessage());
        errorResponse.setStatus(pe.getStatus());
        errorResponse.setTimestamp(new Date());
        return ResponseEntity.status(pe.getStatus()).body(errorResponse);
    }

}
