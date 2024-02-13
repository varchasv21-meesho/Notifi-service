package com.notification.service.v2v.Notifiservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
public class SMSRequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String phoneNumber;
    private String message;
    private String status;
    private String failureCode;
    private String failureComments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SMSRequestEntity() {
    }

    // Constructor with parameters
    public SMSRequestEntity(String phoneNumber, String message, String status, String failureCode, String failureComments) {
        this.phoneNumber = phoneNumber;
        this.message = message;
        this.status = status;
        this.failureCode = failureCode;
        this.failureComments = failureComments;
    }

    public SMSRequestEntity(String phoneNumber, String message) {
        this.phoneNumber = phoneNumber;
        this.message = message;
    }


}
