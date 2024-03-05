package com.notification.service.v2v.Notifiservice.kafka.consumer;

import com.notification.service.v2v.Notifiservice.data.entity.PageDetails;
import com.notification.service.v2v.Notifiservice.data.entity.SmsEntity;
import com.notification.service.v2v.Notifiservice.data.responses.CustomResponse;
import com.notification.service.v2v.Notifiservice.db.mysql.dao.SMSDao;
import com.notification.service.v2v.Notifiservice.exceptionHandling.ErrorResponse;
import com.notification.service.v2v.Notifiservice.exceptionHandling.ValidationException;
import com.notification.service.v2v.Notifiservice.services.BlacklistService;
import com.notification.service.v2v.Notifiservice.services.SmsService;
import com.notification.service.v2v.Notifiservice.services.imiconnect.ThirdPartyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
@Slf4j
public class MessageConsumer {
    private final SmsService smsService;
    private final BlacklistService blacklistService;
    private final SMSDao smsDao;
    private final ThirdPartyService thirdPartyService;

    @Autowired
    public MessageConsumer(BlacklistService blacklistService, SmsService smsService, SMSDao smsDao, ThirdPartyService thirdPartyService) {
        this.blacklistService = blacklistService;
        this.smsService = smsService;
        this.smsDao = smsDao;
        this.thirdPartyService = thirdPartyService;
    }

    @KafkaListener(topics = "${spring.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String id) {
        log.debug("Received message: " + id);
        SmsEntity currentSms;
        try {
            CustomResponse<SmsEntity, String, PageDetails> customResponse = smsService.getSmsRequestById(Long.parseLong(id));
            currentSms = customResponse.getData();
            log.debug(currentSms.toString());
        }catch (Exception e){
            throw new NullPointerException(String.valueOf(e));
        }

        String phoneNo = currentSms.getPhoneNumber();
        if(blacklistService.isBlacklisted(phoneNo)){
            log.debug("the number is blacklisted");
            currentSms.setStatus("BAD_REQUEST");
            currentSms.setFailureCode("400");
            currentSms.setFailureComments("the number is blacklisted");
        }
        else{
            try {
            String response = thirdPartyService.makeAPICall(String.valueOf(currentSms.getId()), currentSms.getPhoneNumber(), currentSms.getMessage());
            log.info("Imiconnect API response: " + response);
            }catch (Exception e){
                log.error(e.getMessage());
                throw new ValidationException(e.getMessage());
            }
            currentSms.setStatus("SENT");
            log.debug("the number is not blacklisted");
        }
        try {
            smsDao.save(currentSms);
            log.info(String.valueOf(currentSms));
            log.info("Sms saved in repository");

        }catch (Exception e){
            log.error(String.valueOf(e));
            throw new ValidationException("smsDao gave error while saving new record: check mysql database errors");
        }



    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(RuntimeException exc){
        ErrorResponse error = new ErrorResponse();
        error.setMessage(exc.getMessage());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }
}
