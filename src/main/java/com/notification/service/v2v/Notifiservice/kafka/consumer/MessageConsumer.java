package com.notification.service.v2v.Notifiservice.kafka.consumer;

import com.notification.service.v2v.Notifiservice.dao.SMSRequestRepository;
import com.notification.service.v2v.Notifiservice.entity.SMSRequestEntity;
import com.notification.service.v2v.Notifiservice.exceptionHandling.ErrorResponse;
import com.notification.service.v2v.Notifiservice.exceptionHandling.ValidationException;
import com.notification.service.v2v.Notifiservice.services.BlacklistService;
import com.notification.service.v2v.Notifiservice.services.SMSRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
public class MessageConsumer {
    private final SMSRequestService smsRequestService;

    private final BlacklistService blacklistService;

    private final SMSRequestRepository smsRequestRepository;

    @Autowired
    public MessageConsumer(BlacklistService blacklistService, SMSRequestService smsRequestService, SMSRequestRepository smsRequestRepository) {
        this.blacklistService = blacklistService;
        this.smsRequestService = smsRequestService;
        this.smsRequestRepository = smsRequestRepository;
    }

    @KafkaListener(topics = "send_sms", groupId = "varchasv8")
    public void listen(String id) {
        System.out.println("Received message: " + id);
        SMSRequestEntity currentSms = smsRequestService.getSmsRequestById(Long.parseLong(id)).getData();
        System.out.println(currentSms);
        String phoneNo = currentSms.getPhoneNumber();
        if(blacklistService.isBlacklisted(phoneNo)){
            System.out.println("the number is blacklisted");
            currentSms.setStatus("BAD_REQUEST");
            currentSms.setFailureCode("400");
            currentSms.setFailureComments("the number is blacklisted");
            smsRequestRepository.save(currentSms);
//            throw new ValidationException("the number is blacklisted");

        }
        else{
            currentSms.setStatus("SENT");
            smsRequestRepository.save(currentSms);
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
