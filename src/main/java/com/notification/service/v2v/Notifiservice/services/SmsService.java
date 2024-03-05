package com.notification.service.v2v.Notifiservice.services;

import com.notification.service.v2v.Notifiservice.data.entity.ESEntity;
import com.notification.service.v2v.Notifiservice.data.entity.PageDetails;
import com.notification.service.v2v.Notifiservice.data.entity.SmsEntity;
import com.notification.service.v2v.Notifiservice.data.requests.ESTextSearchRequest;
import com.notification.service.v2v.Notifiservice.data.requests.ESTimeRangeRequest;
import com.notification.service.v2v.Notifiservice.data.responses.CustomResponse;
import com.notification.service.v2v.Notifiservice.data.responses.SMSResponse;
import com.notification.service.v2v.Notifiservice.db.elasticsearch.dao.ESDao;
import com.notification.service.v2v.Notifiservice.db.mysql.dao.SMSDao;
import com.notification.service.v2v.Notifiservice.exceptionHandling.ErrorResponse;
import com.notification.service.v2v.Notifiservice.exceptionHandling.ValidationException;
import com.notification.service.v2v.Notifiservice.kafka.producer.MessageProducer;
import com.notification.service.v2v.Notifiservice.transformer.SMSRequestToESEntityTransformer;
import com.notification.service.v2v.Notifiservice.validators.PageValidator;
import com.notification.service.v2v.Notifiservice.validators.SmsRequestValidator;
import com.notification.service.v2v.Notifiservice.validators.TimeValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SmsService {
    private final SMSDao smsDao;
    private final ESDao esDao;
    private final BlacklistService blacklistService;
    final MessageProducer messageProducer;

    @Value("${spring.kafka.topic}")
    private String kafkaTopic;

    @Autowired
    public SmsService(SMSDao smsDao, ESDao esDao, BlacklistService blacklistService, MessageProducer messageProducer) {
        this.smsDao = smsDao;
        this.esDao = esDao;
        this.blacklistService = blacklistService;
        this.messageProducer = messageProducer;
    }

    public CustomResponse<List<SmsEntity>,String,PageDetails> getAllSMSRequests() {
        return new CustomResponse<>(smsDao.findAll(),null,null);
    }

    public CustomResponse<SmsEntity,String, PageDetails> getSmsRequestById(Long id) throws ValidationException{
        if(id<0){
            log.error("wrong request id " + id + ": provided id is negative --- id should be > 0");
            throw new ValidationException(new ErrorResponse(400, "provide appropriate request id"));
        }
        Optional<SmsEntity> result;
        try{
            result = smsDao.findById(id);
        }
        catch (Exception e){
            log.debug("smsDao gave error while searching for id " + id + ": check mysql database errors");
            throw new RuntimeException("SMSDao gave error while searching for id " + id + ": check mysql database errors");
        }
        if(result.isPresent()){
            return new CustomResponse<>(result.get(),null,null);
        }else {
            log.error("wrong request id " + id + ": provided id is not found in database");
            throw new IllegalArgumentException(String.valueOf(new ErrorResponse(400, "request_id not found in database")));
        }
    }

    public CustomResponse<SmsEntity,String,PageDetails> deleteSmsRequest(Long id) throws ValidationException{
        if(id<0){
            log.error("wrong request id " + id + ": provided id is negative --- id should be > 0");
            throw new ValidationException(new ErrorResponse(400, "provide appropriate request id"));
        }
        SmsEntity result;
        if (smsDao.existsById(id)) {
            result = smsDao.findById(id).orElse(null);
            smsDao.deleteById(id);
            return new CustomResponse<>(result,null,null);
        } else {
            log.error("wrong request id " + id + ": provided id is not found in database");
            throw new RuntimeException(String.valueOf(new ErrorResponse(400, "request_id not found in database")));
        }
    }

    public SmsEntity setDetails(SmsEntity smsEntity){

        smsEntity.setCreatedAt(LocalDateTime.now());
        smsEntity.setUpdatedAt(LocalDateTime.now());

        String s = smsEntity.getPhoneNumber();
        smsEntity.setPhoneNumber(s.substring(Math.max(0,s.length()-10)));

        return smsEntity;
    }

    public CustomResponse<SMSResponse,String, PageDetails> addSms(SmsEntity smsEntity) {
        if(SmsRequestValidator.isInvalidPhoneNumber(smsEntity.getPhoneNumber())){
            log.error("wrong input format : phone_number validation failed");
            throw new ValidationException(new ErrorResponse(400, "please check phone number"));
        }
        SmsEntity savedSmsEntity;
        try {
            if(blacklistService.isBlacklisted(smsEntity.getPhoneNumber())){
                smsEntity.setStatus("BAD_REQUEST");
                smsEntity.setFailureCode("400");
                smsEntity.setFailureComments("the number is blacklisted");
            }
            else smsEntity.setStatus("Pending");
            smsEntity = setDetails(smsEntity);
            savedSmsEntity = smsDao.save(smsEntity);
        }
        catch (Exception e) {
            log.debug("smsDao gave error while saving new record: check mysql database errors");
            throw new RuntimeException("smsDao gave error while saving new record: check mysql database errors");
        }
        try{
            messageProducer.sendMessage(kafkaTopic,String.valueOf(savedSmsEntity.getId()));
        }catch (Exception e){
            log.debug("messageProducer cant produce messageId: check for kafka");
            throw new RuntimeException("messageProducer cant produce Id for the message : check for kafka");
        }
        ESEntity es = SMSRequestToESEntityTransformer.transformer(savedSmsEntity);
        try {
            esDao.save(es);
            log.info(String.valueOf(es));
        } catch (Exception e) {
            log.error(String.valueOf(e));
            log.error("ESDao : save() call threw error");
            throw new RuntimeException("ESDao : save() call threw error");
        }

        return new CustomResponse<>(new SMSResponse(savedSmsEntity.getId(), "Pending"),null,null);
    }

    public CustomResponse<List<ESEntity>,String, PageDetails> findByPhoneNumberAndTimeRange(ESTimeRangeRequest esTimeRangeRequest) throws ValidationException {
        if(SmsRequestValidator.isInvalidPhoneNumber(esTimeRangeRequest.getPhoneNumber())){
            log.error("Invalid phone number entered");
            throw new ValidationException((new ErrorResponse(400,"Invalid phone number entered")));
        }
        if(TimeValidator.checkEndTimeIsBeforeStartTime(esTimeRangeRequest)){
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
            Page<ESEntity> result = esDao.findByPhoneNumberAndCreatedAtBetween(esTimeRangeRequest.getPhoneNumber(), startTime, endTime, pageRequest);
            return new CustomResponse<>(result.getContent(), null, new PageDetails(result.getNumber(), result.getSize()));
        } catch (Exception e) {
            log.error(String.valueOf(e));
            log.error("ESDao : findByPhoneNumberAndCreatedAtBetweenOrderByCreatedAtDesc() call threw error");
            throw new ValidationException("ESDao : findByPhoneNumberAndCreatedAtBetweenOrderByCreatedAtDesc() call threw error");
        }

    }

    public CustomResponse<List<ESEntity>,String, PageDetails> findByMessage(ESTextSearchRequest esTextSearchRequest) throws ValidationException{
        if(PageValidator.checkPages(esTextSearchRequest.getPageDetails())){
            log.error("Page Details are not according to format");
            throw new ValidationException("Page Index should be non-negative and size must be between (1-50)");
        }
        PageRequest pageRequest = PageRequest.of(esTextSearchRequest.getPageDetails().getPage(), esTextSearchRequest.getPageDetails().getSize());
        try {
            Page<ESEntity> result = esDao.findByMessageContaining(esTextSearchRequest.getText(), pageRequest);
            return new CustomResponse<>(result.getContent(),null,new PageDetails(result.getNumber(), result.getSize()));
        }catch (Exception e){
            log.error(String.valueOf(e));
            log.error("ESDao : findByMessageContaining() call threw error");
            throw new ValidationException("ESDao : findByMessageContaining() call threw error");
        }
    }

    public CustomResponse<List<ESEntity>, String, PageDetails> findAll(PageDetails pageDetails) throws ValidationException{
        PageRequest pageRequest = PageRequest.of(pageDetails.getPage(), pageDetails.getSize());
        try {
            Page<ESEntity> result = esDao.findAll(pageRequest);
            return new CustomResponse<>(result.getContent(), null, new PageDetails(result.getNumber(), result.getSize()));
        } catch (Exception e) {
            log.error(String.valueOf(e));
            log.error("ESDao : findAll() call threw error");
            throw new ValidationException("ESDao : findAll() call threw error");
        }
    }
}