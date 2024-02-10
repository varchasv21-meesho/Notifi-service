package com.notification.service.v2v.Notifiservice.rest.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ElsRequest {
    private String phoneNumber;
    private LocalDateTime startTime;
    private  LocalDateTime endTime;
    private int page;
    private int size;


}
