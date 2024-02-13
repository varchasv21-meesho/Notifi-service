package com.notification.service.v2v.Notifiservice.controllers;

import com.notification.service.v2v.Notifiservice.entity.PageDetails;
import com.notification.service.v2v.Notifiservice.entity.SMSRequestEntity;
//import com.notification.service.v2v.Notifiservice.kafka.producer.MessageProducer;
import com.notification.service.v2v.Notifiservice.exceptionHandling.ErrorResponse;
import com.notification.service.v2v.Notifiservice.exceptionHandling.ValidationException;
import com.notification.service.v2v.Notifiservice.rest.responses.CustomResponse;
import com.notification.service.v2v.Notifiservice.rest.responses.SMSResponse;
import com.notification.service.v2v.Notifiservice.services.SMSRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/sms")
public class SMSRequestController {

    private final SMSRequestService smsRequestService;

//    private final MessageProducer messageProducer;

    @Autowired
    SMSRequestController(SMSRequestService smsRequestService1){
        smsRequestService = smsRequestService1;
    }

    @GetMapping
    public CustomResponse<List<SMSRequestEntity>,String, PageDetails> getAllSmsRequests() {
        return smsRequestService.getAllSMSRequests();
    }

    @GetMapping("/{id}")
    public CustomResponse<SMSRequestEntity, String, PageDetails> getSmsRequestById(@PathVariable Long id) throws ValidationException {
        return smsRequestService.getSmsRequestById(id);
    }

    @PostMapping("/send")
    public CustomResponse<SMSResponse,String,PageDetails> sendSMS(@RequestBody SMSRequestEntity smsRequestEntity) throws ValidationException {
        return smsRequestService.addSmsRequest(smsRequestEntity);
    }

    @DeleteMapping("/{id}")
    public CustomResponse<SMSRequestEntity, String, PageDetails> deleteSmsRequest(@PathVariable Long id) throws ValidationException {
        return smsRequestService.deleteSmsRequest(id);
    }


    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(ValidationException exc){
        ErrorResponse error = new ErrorResponse();
        error.setMessage(exc.getMessage());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }
}
