package com.notification.service.v2v.Notifiservice.controllers;

import com.notification.service.v2v.Notifiservice.entity.SMSRequest;
//import com.notification.service.v2v.Notifiservice.kafka.producer.MessageProducer;
import com.notification.service.v2v.Notifiservice.exceptionHandling.ErrorResponse;
import com.notification.service.v2v.Notifiservice.exceptionHandling.ValidationException;
import com.notification.service.v2v.Notifiservice.services.SMSRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("v1/sms")
public class SMSRequestController {

    private final SMSRequestService smsRequestService;

//    private final MessageProducer messageProducer;

    @Autowired
    SMSRequestController(SMSRequestService smsRequestService1){
        smsRequestService = smsRequestService1;
//        this.messageProducer = messageProducer;
    }

    @GetMapping
    public List<SMSRequest> getAllSmsRequests() {
        return smsRequestService.getAllSMSRequests();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSmsRequestById(@PathVariable Long id) throws ValidationException {
        SMSRequest smsRequest = smsRequestService.getSmsRequestById(id);

        if (smsRequest == null) {
            throw new ValidationException("request_id not found");
        }
        // Request ID found, return success response
        return ResponseEntity.ok(smsRequest);
    }

    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendSMS(@RequestBody SMSRequest smsRequest) throws ValidationException {
        if(smsRequest.getPhoneNumber()==null) {
            System.out.println("The number is " + null);
            throw new ValidationException("Phone number is mandatory");
        }

        SMSRequest newSms = smsRequestService.addSmsRequest(smsRequest);



//        messageProducer.sendMessage("send_sms",String.valueOf(newSms.getId()));

        Map<String, Object> successResponse = new HashMap<>();
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("requestId", newSms.getId());
        responseData.put("comments", "Successfully Sent");
        successResponse.put("data", responseData);
        return ResponseEntity.ok(successResponse);
    }

    @DeleteMapping("/{id}")
    public void deleteSmsRequest(@PathVariable Long id) throws ValidationException {
        smsRequestService.deleteSmsRequest(id);
    }


    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(ValidationException exc){
        ErrorResponse error = new ErrorResponse();
        error.setMessage(exc.getMessage());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }
}
