package com.notification.service.v2v.Notifiservice.rest.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SMSResponse {
    Long requestId;
    String comments;
}
