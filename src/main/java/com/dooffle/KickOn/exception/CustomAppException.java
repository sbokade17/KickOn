package com.dooffle.KickOn.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomAppException extends RuntimeException {


    private static final long serialVersionUID = -5295235692274246795L;
    private final int status;
    private final String error;

    public CustomAppException(HttpStatus status, String message) {
        super(message);
        this.status = status.value();
        this.error = status.getReasonPhrase();
    }

}