package com.notification.service.v2v.Notifiservice.validators;

import com.notification.service.v2v.Notifiservice.data.requests.ESTimeRangeRequest;

public class TimeValidator {
    public static boolean checkEndTimeIsBeforeStartTime(ESTimeRangeRequest esTimeRangeRequest){
        return esTimeRangeRequest.getEndTime().isBefore(esTimeRangeRequest.getStartTime());
    }
}
