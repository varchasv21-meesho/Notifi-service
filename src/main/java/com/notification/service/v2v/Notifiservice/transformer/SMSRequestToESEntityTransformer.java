package com.notification.service.v2v.Notifiservice.transformer;

import com.notification.service.v2v.Notifiservice.entity.ESEntity;
import com.notification.service.v2v.Notifiservice.entity.SMSRequest;

import java.sql.Timestamp;

public class SMSRequestToESEntityTransformer {
    public static ESEntity transformer(SMSRequest smsRequest){
        ESEntity esEntity = new ESEntity();
        esEntity.setId(smsRequest.getId());
        esEntity.setMessage(smsRequest.getMessage());
        esEntity.setPhoneNumber(smsRequest.getPhoneNumber());
        esEntity.setStatus(smsRequest.getStatus());
        esEntity.setCreatedAt(Timestamp.valueOf(smsRequest.getCreatedAt()).getTime());
        esEntity.setUpdatedAt(Timestamp.valueOf(smsRequest.getUpdatedAt()).getTime());
        return esEntity;
    }
}
