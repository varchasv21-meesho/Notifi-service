package com.notification.service.v2v.Notifiservice.data.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.notification.service.v2v.Notifiservice.data.entity.PageDetails;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ESTimeRangeRequest {
    private String phoneNumber;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private PageDetails pageDetails;


}
