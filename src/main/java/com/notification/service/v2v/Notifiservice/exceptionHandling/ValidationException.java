package com.notification.service.v2v.Notifiservice.exceptionHandling;

public class ValidationException extends Exception{

    ErrorResponse errorResponse;
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(ErrorResponse errorResponse) {this.errorResponse = errorResponse;}

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(Throwable cause) {
        super(cause);
    }
}
