package com.notification.service.v2v.Notifiservice.services;

import com.notification.service.v2v.Notifiservice.dao.ESRepository;
import com.notification.service.v2v.Notifiservice.entity.ESEntity;
import com.notification.service.v2v.Notifiservice.entity.PageDetails;
import com.notification.service.v2v.Notifiservice.entity.SMSRequest;
import com.notification.service.v2v.Notifiservice.exceptionHandling.ErrorResponse;
import com.notification.service.v2v.Notifiservice.exceptionHandling.ValidationException;
import com.notification.service.v2v.Notifiservice.rest.requests.ESTextSearchRequest;
import com.notification.service.v2v.Notifiservice.rest.requests.ESTimeRangeRequest;
//import com.notification.service.v2v.Notifiservice.rest.responses.ElsResponse;
import com.notification.service.v2v.Notifiservice.rest.responses.CustomResponse;
import com.notification.service.v2v.Notifiservice.transformer.SMSRequestToESEntityTransformer;
import com.notification.service.v2v.Notifiservice.validators.SmsRequestValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ESService {
    private final ESRepository esRepository;

    public void save(SMSRequest smsRequest) throws ValidationException {
        ESEntity es = SMSRequestToESEntityTransformer.transformer(smsRequest);
        try {
            esRepository.save(es);
        }catch (Exception e){
            System.out.println(e);
            log.error("ESRepository : save() call threw error");
            throw new ValidationException("ESRepository : save() call threw error");
        }
    }

    public CustomResponse<List<ESEntity>,String, PageDetails> findByPhoneNumberAndTimeRange(ESTimeRangeRequest esTimeRangeRequest) throws ValidationException {
        if(SmsRequestValidator.isInvalidPhoneNumber(esTimeRangeRequest.getPhoneNumber())){
            log.error("Invalid phone number entered");
            throw new ValidationException((new ErrorResponse(400,"Invalid phone number entered")));
        }
        PageRequest pageRequest = PageRequest.of(esTimeRangeRequest.getPageDetails().getPage(),esTimeRangeRequest.getPageDetails().getSize());

        try {
            Page<ESEntity> result = esRepository.findByPhoneNumberAndCreatedAtBetweenOrderByCreatedAtDesc(esTimeRangeRequest.getPhoneNumber(), esTimeRangeRequest.getStartTime(), esTimeRangeRequest.getEndTime(), pageRequest);
            return new CustomResponse<>(result.getContent(), null, new PageDetails(result.getNumber(), result.getSize()));
        } catch (Exception e) {
            log.error("ESRepository : findByPhoneNumberAndCreatedAtBetweenOrderByCreatedAtDesc() call threw error");
            throw new ValidationException("ESRepository : findByPhoneNumberAndCreatedAtBetweenOrderByCreatedAtDesc() call threw error");
        }

    }

    public CustomResponse<List<ESEntity>,String, PageDetails> findByMessage(ESTextSearchRequest esTextSearchRequest) throws ValidationException{
        PageRequest pageRequest = PageRequest.of(esTextSearchRequest.getPageDetails().getPage(), esTextSearchRequest.getPageDetails().getSize());
        try {
            Page<ESEntity> result = esRepository.findByMessageContaining(esTextSearchRequest.getText(), pageRequest);
            return new CustomResponse<>(result.getContent(),null,new PageDetails(result.getNumber(), result.getSize()));
        }catch (Exception e){
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
            log.error("ESRepository : findAll() call threw error");
            throw new ValidationException("ESRepository : findAll() call threw error");
        }
    }
}
