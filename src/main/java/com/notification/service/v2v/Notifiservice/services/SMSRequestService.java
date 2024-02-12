package com.notification.service.v2v.Notifiservice.services;

import com.notification.service.v2v.Notifiservice.dao.SMSRequestRepository;
import com.notification.service.v2v.Notifiservice.entity.PageDetails;
import com.notification.service.v2v.Notifiservice.entity.SMSRequestEntity;
import com.notification.service.v2v.Notifiservice.exceptionHandling.ErrorResponse;
import com.notification.service.v2v.Notifiservice.exceptionHandling.ValidationException;
import com.notification.service.v2v.Notifiservice.kafka.producer.MessageProducer;
import com.notification.service.v2v.Notifiservice.rest.responses.CustomResponse;
import com.notification.service.v2v.Notifiservice.rest.responses.SMSResponse;
import com.notification.service.v2v.Notifiservice.validators.SmsRequestValidator;
import lombok.extern.slf4j.Slf4j;
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

    public CustomResponse<List<SMSRequestEntity>,String,PageDetails> getAllSMSRequests() {
        return new CustomResponse<>(smsRequestRepository.findAll(),null,null);
    }

    public CustomResponse<SMSRequestEntity,String, PageDetails> getSmsRequestById(Long id) throws ValidationException{
        if(id<0){
            log.error("wrong request id " + id + ": provided id is negative --- id should be > 0");
            throw new ValidationException(new ErrorResponse(400, "provide appropriate request id"));
        }
        Optional<SMSRequestEntity> result;
        try{
            result = smsRequestRepository.findById(id);
        }
        catch (Exception e){
            log.debug("smsRepository gave error while searching for id " + id + ": check mysql database errors");
            throw new ValidationException("SMSRequestRepository gave error while searching for id " + id + ": check mysql database errors");
        }
        if(result.isPresent()){
            return new CustomResponse<>(result.get(),null,null);
        }else {
            log.error("wrong request id " + id + ": provided id is not found in database");
            throw new ValidationException( new ErrorResponse(400, "request_id not found in database"));
        }
    }

    public CustomResponse<SMSRequestEntity,String,PageDetails> deleteSmsRequest(Long id) throws ValidationException{
        if(id<0){
            log.error("wrong request id " + id + ": provided id is negative --- id should be > 0");
            throw new ValidationException(new ErrorResponse(400, "provide appropriate request id"));
        }
        SMSRequestEntity result;
        if (!smsRequestRepository.existsById(id)) {
            log.error("wrong request id " + id + ": provided id is not found in database");
            throw new ValidationException( new ErrorResponse(400, "request_id not found in database"));
        } else {
            result = smsRequestRepository.findById(id).orElse(null);
            smsRequestRepository.deleteById(id);
            return new CustomResponse<>(result,null,null);
        }
    }

    public SMSRequestEntity setDetails(SMSRequestEntity smsRequestEntity){
        smsRequestEntity.setStatus("PENDING");
        smsRequestEntity.setCreatedAt(LocalDateTime.now());
        smsRequestEntity.setUpdatedAt(LocalDateTime.now());
        String s = smsRequestEntity.getPhoneNumber();
        smsRequestEntity.setPhoneNumber(s.substring(Math.max(0,s.length()-10)));

        return smsRequestEntity;
    }

    public CustomResponse<SMSResponse,String, PageDetails> addSmsRequest(SMSRequestEntity smsRequestEntity) throws ValidationException{
        if(SmsRequestValidator.isInvalidPhoneNumber(smsRequestEntity.getPhoneNumber())){
            log.error("wrong input format : phone_number validation failed");
            throw new ValidationException(new ErrorResponse(400, "please check phone number"));
        }
        SMSRequestEntity savedSmsRequestEntity;
        try {
            savedSmsRequestEntity = smsRequestRepository.save(setDetails(smsRequestEntity));
        }
        catch (Exception e) {
            log.debug("smsRequestRepository gave error while creating new record: check mysql database errors");
            throw new ValidationException("smsRequestRepository gave error while creating new record: check mysql database errors");
        }
        try{
            messageProducer.sendMessage("send_sms",String.valueOf(savedSmsRequestEntity.getId()));
        }catch (Exception e){
            log.debug("messageProducer cant produce messageId: check for kafka");
            throw new ValidationException("messageProducer cant produce Id for the message : check for kafka");
        }

        return new CustomResponse<>(new SMSResponse(savedSmsRequestEntity.getId(), "Pending"),null,null);
    }
}