package com.notification.service.v2v.Notifiservice.rest.responses;

import com.notification.service.v2v.Notifiservice.entity.ESEntity;
import lombok.Setter;

import java.util.List;

@Setter
public class ElsResponse {
    private List<ESEntity> messages;
    private int totalPages;
    private int totalElements;

}
