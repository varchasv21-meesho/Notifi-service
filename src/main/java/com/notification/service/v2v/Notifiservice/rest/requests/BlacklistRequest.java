package com.notification.service.v2v.Notifiservice.rest.requests;

import java.util.Set;

public class BlacklistRequest {

    private Set<String> phoneNumbers;

    public Set<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(Set<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }
}