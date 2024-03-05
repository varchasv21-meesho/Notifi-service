package com.notification.service.v2v.Notifiservice.data.entity.imiconnect;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Channels {
    @JsonProperty("sms")
    private Sms sms;
}
