package com.notification.service.v2v.Notifiservice.validators;

import java.util.Set;
import java.util.regex.Pattern;

public class SmsRequestValidator {
    private static final Pattern INDIAN_PHONE_NUMBER_PATTERN = Pattern.compile("^\\+91[6-9]\\d{9}$");
    private static final Pattern INDIAN_PHONE_NUMBER_PATTERN2 = Pattern.compile("^[6-9]\\d{9}$");


    public static boolean isInvalidPhoneNumber(String phoneNumber) {
        if(phoneNumber == null || phoneNumber.isEmpty()) return true;
        return !INDIAN_PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches() && !INDIAN_PHONE_NUMBER_PATTERN2.matcher(phoneNumber).matches();
    }

    public static boolean checkAllPhoneNumber(Set<String> phoneNumbers){
        if(phoneNumbers == null || phoneNumbers.isEmpty()) return true;
        for(String phoneNumber: phoneNumbers){
            if(isInvalidPhoneNumber(phoneNumber)) return true;
        }
        return false;
    }
}
