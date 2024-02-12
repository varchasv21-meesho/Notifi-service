package com.notification.service.v2v.Notifiservice.exceptionHandling;

public class ValidationException extends RuntimeException{

    ErrorResponse errorResponse;
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(ErrorResponse errorResponse) {this.errorResponse = errorResponse;}

}
