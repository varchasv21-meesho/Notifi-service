package com.notification.service.v2v.Notifiservice.data.requests;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class BlacklistRequest {

    private Set<String> phoneNumbers;

}