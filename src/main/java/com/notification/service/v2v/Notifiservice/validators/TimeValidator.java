package com.notification.service.v2v.Notifiservice.validators;

import com.notification.service.v2v.Notifiservice.rest.requests.ESTimeRangeRequest;

public class TimeValidator {
    public static boolean checkTimings(ESTimeRangeRequest esTimeRangeRequest){
        return !esTimeRangeRequest.getEndTime().isAfter(esTimeRangeRequest.getStartTime());
    }
}
