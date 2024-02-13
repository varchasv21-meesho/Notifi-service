package com.notification.service.v2v.Notifiservice.exceptionHandling;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException{

    ErrorResponse errorResponse;
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(ErrorResponse errorResponse) {this.errorResponse = errorResponse;}

}
