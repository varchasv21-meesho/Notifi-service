package com.notification.service.v2v.Notifiservice.data.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.notification.service.v2v.Notifiservice.data.entity.PageDetails;
import lombok.*;
import org.jetbrains.annotations.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ESTextSearchRequest {
    @NotNull("Message should not be null")
    private String text;
    private PageDetails pageDetails;
}
