package com.notification.service.v2v.Notifiservice.kafka.consumer;

import com.notification.service.v2v.Notifiservice.db.mysql.repository.SMSRepository;
import com.notification.service.v2v.Notifiservice.data.entity.SmsEntity;
import com.notification.service.v2v.Notifiservice.exceptionHandling.ErrorResponse;
import com.notification.service.v2v.Notifiservice.exceptionHandling.ValidationException;
import com.notification.service.v2v.Notifiservice.services.BlacklistService;
import com.notification.service.v2v.Notifiservice.services.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
public class MessageConsumer {
    private final SmsService smsService;

    private final BlacklistService blacklistService;

    private final SMSRepository smsRepository;

    @Autowired
    public MessageConsumer(BlacklistService blacklistService, SmsService smsService, SMSRepository smsRepository) {
        this.blacklistService = blacklistService;
        this.smsService = smsService;
        this.smsRepository = smsRepository;
    }

    @KafkaListener(topics = "send_sms", groupId = "varchasv8")
    public void listen(String id) {
        System.out.println("Received message: " + id);
        SmsEntity currentSms = smsService.getSmsRequestById(Long.parseLong(id)).getData();
        System.out.println(currentSms);
        String phoneNo = currentSms.getPhoneNumber();
        if(blacklistService.isBlacklisted(phoneNo)){
            System.out.println("the number is blacklisted");
            currentSms.setStatus("BAD_REQUEST");
            currentSms.setFailureCode("400");
            currentSms.setFailureComments("the number is blacklisted");
            smsRepository.save(currentSms);
//            throw new ValidationException("the number is blacklisted");

        }
        else{
            currentSms.setStatus("SENT");
            smsRepository.save(currentSms);
            System.out.println("the number is not blacklisted");
//            System.out.println("sending sms!!!");
        }



    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(ValidationException exc){
        ErrorResponse error = new ErrorResponse();
        error.setMessage(exc.getMessage());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }
}
