package com.notification.service.v2v.Notifiservice.transformer;

import com.notification.service.v2v.Notifiservice.data.entity.ESEntity;
import com.notification.service.v2v.Notifiservice.data.entity.SmsEntity;

import java.sql.Timestamp;

public class SMSRequestToESEntityTransformer {
    public static ESEntity transformer(SmsEntity smsEntity){
        ESEntity esEntity = new ESEntity();
        esEntity.setId(smsEntity.getId());
        esEntity.setMessage(smsEntity.getMessage());
        esEntity.setPhoneNumber(smsEntity.getPhoneNumber());
        esEntity.setStatus(smsEntity.getStatus());
        esEntity.setCreatedAt(Timestamp.valueOf(smsEntity.getCreatedAt()).getTime());
        esEntity.setUpdatedAt(Timestamp.valueOf(smsEntity.getUpdatedAt()).getTime());
        esEntity.setFailureCode(smsEntity.getFailureCode());
        esEntity.setFailureComment(smsEntity.getFailureComments());
//        System.out.println(esEntity);
        return esEntity;
    }
}
