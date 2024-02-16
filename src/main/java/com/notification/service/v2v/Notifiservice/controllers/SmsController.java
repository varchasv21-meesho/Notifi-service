package com.notification.service.v2v.Notifiservice.controllers;

import com.notification.service.v2v.Notifiservice.data.entity.ESEntity;
import com.notification.service.v2v.Notifiservice.data.entity.PageDetails;
import com.notification.service.v2v.Notifiservice.data.entity.SmsEntity;
//import com.notification.service.v2v.Notifiservice.kafka.producer.MessageProducer;
import com.notification.service.v2v.Notifiservice.data.requests.ESTextSearchRequest;
import com.notification.service.v2v.Notifiservice.data.requests.ESTimeRangeRequest;
import com.notification.service.v2v.Notifiservice.exceptionHandling.ErrorResponse;
import com.notification.service.v2v.Notifiservice.exceptionHandling.ValidationException;
import com.notification.service.v2v.Notifiservice.data.responses.CustomResponse;
import com.notification.service.v2v.Notifiservice.data.responses.SMSResponse;
import com.notification.service.v2v.Notifiservice.services.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("v1/sms")
public class SmsController {

    private final SmsService smsService;

//    private final MessageProducer messageProducer;

    @Autowired
    SmsController(SmsService smsService1){
        smsService = smsService1;
    }

    @GetMapping
    public CustomResponse<List<SmsEntity>,String, PageDetails> getAllSmsRequests() {
        return smsService.getAllSMSRequests();
    }

    @GetMapping("/{id}")
    public CustomResponse<SmsEntity, String, PageDetails> getSmsRequestById(@PathVariable Long id) throws ValidationException {
        return smsService.getSmsRequestById(id);
    }

    @PostMapping("/send")
    public CustomResponse<SMSResponse,String,PageDetails> sendSMS(@RequestBody SmsEntity smsEntity) throws ValidationException {
        return smsService.addSms(smsEntity);
    }

    @DeleteMapping("/{id}")
    public CustomResponse<SmsEntity, String, PageDetails> deleteSmsRequest(@PathVariable Long id) throws ValidationException {
        return smsService.deleteSmsRequest(id);
    }

    // Elasticsearch controllers
    @GetMapping("/findAll")
    public CustomResponse<List<ESEntity>,String, PageDetails> findAll(@RequestBody PageDetails pageDetails) throws ValidationException {
        return smsService.findAll(pageDetails);
    }

    // Search Query to retrieve all messages by a phoneNumber given startTime and endTime.
    @GetMapping("/search-by-time")
    public CustomResponse<List<ESEntity>, String, PageDetails> searchSmsByTimeRange(@Valid @RequestBody ESTimeRangeRequest esTimeRangeRequest) throws ValidationException{
        return smsService.findByPhoneNumberAndTimeRange(esTimeRangeRequest);
    }

    // Search Query to retrieve all messages given Search text
    @GetMapping("/search-by-text")
    public CustomResponse<List<ESEntity>, String, PageDetails> getSmsContainingText(@Valid @RequestBody ESTextSearchRequest esTextSearchRequest) throws ValidationException {
        return smsService.findByMessage(esTextSearchRequest);
    }


    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(ValidationException exc){
        ErrorResponse error = new ErrorResponse();
        error.setMessage(exc.getMessage());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }
}
