package com.notification.service.v2v.Notifiservice.rest.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomResponse<T1, T2, T3> {
    T1 data;
    T2 error;
    T3 pageDetails;
}
