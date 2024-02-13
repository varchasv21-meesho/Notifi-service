package com.notification.service.v2v.Notifiservice.services;

import com.notification.service.v2v.Notifiservice.dao.ESRepository;
import com.notification.service.v2v.Notifiservice.dao.SMSRequestRepository;
import com.notification.service.v2v.Notifiservice.entity.ESEntity;
import com.notification.service.v2v.Notifiservice.entity.PageDetails;
import com.notification.service.v2v.Notifiservice.entity.SMSRequestEntity;
import com.notification.service.v2v.Notifiservice.exceptionHandling.ErrorResponse;
import com.notification.service.v2v.Notifiservice.exceptionHandling.ValidationException;
import com.notification.service.v2v.Notifiservice.kafka.producer.MessageProducer;
import com.notification.service.v2v.Notifiservice.rest.requests.ESTextSearchRequest;
import com.notification.service.v2v.Notifiservice.rest.requests.ESTimeRangeRequest;
import com.notification.service.v2v.Notifiservice.rest.responses.CustomResponse;
import com.notification.service.v2v.Notifiservice.transformer.SMSRequestToESEntityTransformer;
import com.notification.service.v2v.Notifiservice.validators.PageValidator;
import com.notification.service.v2v.Notifiservice.validators.SmsRequestValidator;
import com.notification.service.v2v.Notifiservice.validators.TimeValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ESService {
    private final ESRepository esRepository;
    private final SMSRequestRepository smsRequestRepository;
    private final SMSRequestService smsRequestService;
    private final MessageProducer messageProducer;

    public void save(SMSRequestEntity smsRequestEntity) throws ValidationException {
        if (!SmsRequestValidator.isInvalidPhoneNumber(smsRequestEntity.getPhoneNumber())) {
            SMSRequestEntity result;
            try {
                result = smsRequestRepository.save(smsRequestService.setDetails(smsRequestEntity));
            }catch (Exception e){
                log.error(String.valueOf(e));
                log.error("SMSRequestRepository : save() call threw error");
                throw new ValidationException("SMSRequestRepository : save() call threw error");
            }
            try{
                messageProducer.sendMessage("send_sms",String.valueOf(result.getId()));
            }catch (Exception e){
                log.debug("messageProducer cant produce messageId: check for kafka");
                throw new ValidationException("messageProducer cant produce Id for the message : check for kafka");
            }
            ESEntity es = SMSRequestToESEntityTransformer.transformer(result);
            try {
                esRepository.save(es);
                log.info(String.valueOf(es));
//                System.out.println(esRepository.findAll());
            } catch (Exception e) {
                log.error(String.valueOf(e));
                log.error("ESRepository : save() call threw error");
                throw new ValidationException("ESRepository : save() call threw error");
            }
        } else {
            log.error("Phone number not in valid format");
            throw new ValidationException("Please enter valid phone number");
        }
    }

    public CustomResponse<List<ESEntity>,String, PageDetails> findByPhoneNumberAndTimeRange(ESTimeRangeRequest esTimeRangeRequest) throws ValidationException {
        if(SmsRequestValidator.isInvalidPhoneNumber(esTimeRangeRequest.getPhoneNumber())){
            log.error("Invalid phone number entered");
            throw new ValidationException((new ErrorResponse(400,"Invalid phone number entered")));
        }
        if(TimeValidator.checkTimings(esTimeRangeRequest)){
            log.error("endTime is before the startTime");
            throw new ValidationException("endTime is before the startTime");
        }
        if(PageValidator.checkPages(esTimeRangeRequest.getPageDetails())){
            log.error("Page Details are not according to format");
            throw new ValidationException("Page Index should be non-negative and size must be between (1-50)");
        }
        PageRequest pageRequest = PageRequest.of(esTimeRangeRequest.getPageDetails().getPage(),esTimeRangeRequest.getPageDetails().getSize());

        try {
            Long startTime = Timestamp.valueOf(esTimeRangeRequest.getStartTime()).getTime();
            Long endTime = Timestamp.valueOf(esTimeRangeRequest.getEndTime()).getTime();
            Page<ESEntity> result = esRepository.findByPhoneNumberAndCreatedAtBetween(esTimeRangeRequest.getPhoneNumber(), startTime, endTime, pageRequest);
            return new CustomResponse<>(result.getContent(), null, new PageDetails(result.getNumber(), result.getSize()));
        } catch (Exception e) {
            log.error(String.valueOf(e));
            log.error("ESRepository : findByPhoneNumberAndCreatedAtBetweenOrderByCreatedAtDesc() call threw error");
            throw new ValidationException("ESRepository : findByPhoneNumberAndCreatedAtBetweenOrderByCreatedAtDesc() call threw error");
        }

    }

    public CustomResponse<List<ESEntity>,String, PageDetails> findByMessage(ESTextSearchRequest esTextSearchRequest) throws ValidationException{
        if(PageValidator.checkPages(esTextSearchRequest.getPageDetails())){
            log.error("Page Details are not according to format");
            throw new ValidationException("Page Index should be non-negative and size must be between (1-50)");
        }
        PageRequest pageRequest = PageRequest.of(esTextSearchRequest.getPageDetails().getPage(), esTextSearchRequest.getPageDetails().getSize());
        try {
            Page<ESEntity> result = esRepository.findByMessageContaining(esTextSearchRequest.getText(), pageRequest);
            return new CustomResponse<>(result.getContent(),null,new PageDetails(result.getNumber(), result.getSize()));
        }catch (Exception e){
            log.error(String.valueOf(e));
            log.error("ESRepository : findByMessageContaining() call threw error");
            throw new ValidationException("ESRepository : findByMessageContaining() call threw error");
        }
    }

    public CustomResponse<List<ESEntity>, String, PageDetails> findAll(PageDetails pageDetails) throws ValidationException{
        PageRequest pageRequest = PageRequest.of(pageDetails.getPage(), pageDetails.getSize());
        try {
            Page<ESEntity> result = esRepository.findAll(pageRequest);
            return new CustomResponse<>(result.getContent(), null, new PageDetails(result.getNumber(), result.getSize()));
        } catch (Exception e) {
            log.error(String.valueOf(e));
            log.error("ESRepository : findAll() call threw error");
            throw new ValidationException("ESRepository : findAll() call threw error");
        }
    }
}
