package com.notification.service.v2v.Notifiservice.data.entity.imiconnect;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmsModal {
    @JsonProperty("deliverychannel")
    private String deliveryChannel;

    @JsonProperty("channels")
    private Channels channels;

    @JsonProperty("destination")
    private List<Destination> destination;
}
