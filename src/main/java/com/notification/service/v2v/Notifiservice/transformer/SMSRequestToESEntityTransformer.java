package com.notification.service.v2v.Notifiservice.transformer;

import com.notification.service.v2v.Notifiservice.entity.ESEntity;
import com.notification.service.v2v.Notifiservice.entity.SMSRequestEntity;

import java.sql.Timestamp;

public class SMSRequestToESEntityTransformer {
    public static ESEntity transformer(SMSRequestEntity smsRequestEntity){
        ESEntity esEntity = new ESEntity();
        esEntity.setId(smsRequestEntity.getId());
        esEntity.setMessage(smsRequestEntity.getMessage());
        esEntity.setPhoneNumber(smsRequestEntity.getPhoneNumber());
        esEntity.setStatus(smsRequestEntity.getStatus());
        esEntity.setCreatedAt(Timestamp.valueOf(smsRequestEntity.getCreatedAt()).getTime());
        esEntity.setUpdatedAt(Timestamp.valueOf(smsRequestEntity.getUpdatedAt()).getTime());
        esEntity.setFailureCode(smsRequestEntity.getFailureCode());
        esEntity.setFailureComment(smsRequestEntity.getFailureComments());
//        System.out.println(esEntity);
        return esEntity;
    }
}
