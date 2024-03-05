package com.notification.service.v2v.Notifiservice.data.entity.imiconnect;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Destination {
    @JsonProperty("msisdn")
    private List<String> msisdn;

    @JsonProperty("correlationId")
    private String correlationId;
}
