package com.notification.service.v2v.Notifiservice.controllers;

import com.notification.service.v2v.Notifiservice.entity.ESEntity;
import com.notification.service.v2v.Notifiservice.entity.PageDetails;
import com.notification.service.v2v.Notifiservice.entity.SMSRequest;
import com.notification.service.v2v.Notifiservice.exceptionHandling.ErrorResponse;
import com.notification.service.v2v.Notifiservice.exceptionHandling.ValidationException;
import com.notification.service.v2v.Notifiservice.rest.requests.ESTextSearchRequest;
import com.notification.service.v2v.Notifiservice.rest.requests.ESTimeRangeRequest;
import com.notification.service.v2v.Notifiservice.rest.responses.CustomResponse;
import com.notification.service.v2v.Notifiservice.services.ESService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/sms_els")
public class ESController {

    private final ESService esService;

    // Constructor Injection
    @Autowired
    public ESController(ESService smsRequestServiceEls) {
        this.esService = smsRequestServiceEls;
    }


    @PostMapping("/save-message")
    public void addSMS(@RequestBody SMSRequest smsRequest) throws ValidationException {
        smsRequest.setCreatedAt(LocalDateTime.now());
        smsRequest.setUpdatedAt(LocalDateTime.now());
        esService.save(smsRequest);
    }
    // Retrieving all the messages
    @GetMapping("/findAll")
    public CustomResponse<List<ESEntity>,String, PageDetails> findAll(@RequestBody PageDetails pageDetails) throws ValidationException {
        return esService.findAll(pageDetails);
    }

    // Search Query to retrieve all messages by a phoneNumber given startTime and endTime.
    @GetMapping("/search-by-time")
    public CustomResponse<List<ESEntity>, String, PageDetails> searchSmsByTimeRange(@Valid @RequestBody ESTimeRangeRequest esTimeRangeRequest) throws ValidationException{
        return esService.findByPhoneNumberAndTimeRange(esTimeRangeRequest);
    }


    @GetMapping("/search-by-text")
    public CustomResponse<List<ESEntity>, String, PageDetails> getSmsContainingText(@Valid @RequestBody ESTextSearchRequest esTextSearchRequest) throws ValidationException {
        return esService.findByMessage(esTextSearchRequest);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(ValidationException exc){
        ErrorResponse error = new ErrorResponse();
        error.setMessage(exc.getMessage());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }
}
