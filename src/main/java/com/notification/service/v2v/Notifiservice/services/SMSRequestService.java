package com.notification.service.v2v.Notifiservice.services;

import com.notification.service.v2v.Notifiservice.dao.SMSRequestRepository;
import com.notification.service.v2v.Notifiservice.entity.SMSRequest;
import com.notification.service.v2v.Notifiservice.exceptionHandling.ErrorResponse;
import com.notification.service.v2v.Notifiservice.exceptionHandling.ValidationException;
import com.notification.service.v2v.Notifiservice.kafka.producer.MessageProducer;
import com.notification.service.v2v.Notifiservice.validators.SmsRequestValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SMSRequestService {
    private final SMSRequestRepository smsRequestRepository;

    final MessageProducer messageProducer;

    public SMSRequestService(SMSRequestRepository smsRequestRepository, MessageProducer messageProducer) {
        this.smsRequestRepository = smsRequestRepository;
        this.messageProducer = messageProducer;
    }

    public List<SMSRequest> getAllSMSRequests() { return smsRequestRepository.findAll();}

    public SMSRequest getSmsRequestById(Long id) throws ValidationException{
        if(id<0){
            log.error("wrong request id " + id + ": provided id is negative --- id should be > 0");
            throw new ValidationException(new ErrorResponse(400, "provide appropriate request id"));
        }
        Optional<SMSRequest> result;
        try{
            result = smsRequestRepository.findById(id);
        }
        catch (Exception e){
            log.debug("smsRepository gave error while searching for id " + id + ": check mysql database errors");
            throw new ValidationException("SMSRequestRepository gave error while searching for id " + id + ": check mysql database errors");
        }
        if(result.isPresent()){
            return result.get();
        }else {
            log.error("wrong request id " + id + ": provided id is not found in database");
            throw new ValidationException( new ErrorResponse(400, "request_id not found in database"));
        }
    }

    public SMSRequest addSmsRequest(SMSRequest smsRequest) throws ValidationException{
        if(SmsRequestValidator.isInvalidPhoneNumber(smsRequest.getPhoneNumber())){
            log.error("wrong input format : phone_number validation failed");
            throw new ValidationException(new ErrorResponse(400, "please check phone number"));
        }
        SMSRequest savedSmsRequest;
        smsRequest.setStatus("PENDING");
        smsRequest.setCreatedAt(LocalDateTime.now());
        smsRequest.setUpdatedAt(LocalDateTime.now());
        String s = smsRequest.getPhoneNumber();
        smsRequest.setPhoneNumber(s.substring(Math.max(0,s.length()-10)));
        try {
            savedSmsRequest = smsRequestRepository.save(smsRequest);
        }
        catch (Exception e) {
            log.debug("smsRequestRepository gave error while creating new record: check mysql database errors");
            throw new ValidationException("smsRequestRepository gave error while creating new record: check mysql database errors");
        }
        try{
            messageProducer.sendMessage("send_sms",String.valueOf(savedSmsRequest.getId()));
        }catch (Exception e){
            log.debug("messageProducer cant produce messageId: check for kafka");
            throw new ValidationException("messageProducer cant produce Id for the message : check for kafka");
        }

        return savedSmsRequest;
    }

    public void deleteSmsRequest(Long id) throws ValidationException{
        if(id<0){
            log.error("wrong request id " + id + ": provided id is negative --- id should be > 0");
            throw new ValidationException(new ErrorResponse(400, "provide appropriate request id"));
        }
        if (!smsRequestRepository.existsById(id)) {
            log.error("wrong request id " + id + ": provided id is not found in database");
            throw new ValidationException( new ErrorResponse(400, "request_id not found in database"));
        } else {
            smsRequestRepository.deleteById(id);
        }
    }
}