package com.notification.service.v2v.Notifiservice.rest.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.notification.service.v2v.Notifiservice.entity.PageDetails;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ESTimeRangeRequest {
    @NotBlank(message = "phoneNumber must not be blank")
    private String phoneNumber;
    @NotBlank(message = "startTime must not be blank")
    private LocalDateTime startTime;
    @NotBlank(message = "endTime must not be blank")
    private LocalDateTime endTime;
    @NotBlank(message = "pageDetails must not be blank")
    private PageDetails pageDetails;


}
