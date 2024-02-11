package com.notification.service.v2v.Notifiservice.rest.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.notification.service.v2v.Notifiservice.entity.PageDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ESTextSearchRequest {
    @NotNull("Message should not be null")
    String text;
    PageDetails pageDetails;
}
