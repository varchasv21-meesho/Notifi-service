package com.notification.service.v2v.Notifiservice.data.entity.imiconnect;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sms {
    @JsonProperty("text")
    private String text;
}
