package com.notification.service.v2v.Notifiservice.controllers;

import com.notification.service.v2v.Notifiservice.entity.SMSRequestEls;
import com.notification.service.v2v.Notifiservice.exceptionHandling.ErrorResponse;
import com.notification.service.v2v.Notifiservice.exceptionHandling.ValidationException;
import com.notification.service.v2v.Notifiservice.rest.requests.ElsRequest;
import com.notification.service.v2v.Notifiservice.services.SMSRequestServiceEls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/sms_els")
public class SMSRequestControllerEls {

    private final SMSRequestServiceEls smsRequestServiceEls;

    // Constructor Injection
    @Autowired
    public SMSRequestControllerEls(SMSRequestServiceEls smsRequestServiceEls) {
        this.smsRequestServiceEls = smsRequestServiceEls;
    }


    // Retrieving all the messages
    @GetMapping("/findAll")
    public Iterable<SMSRequestEls> findAll(){
        return smsRequestServiceEls.getAllSMSRequestsEls();
    }

    // Retrieving by ID.
    @GetMapping("/find/{Id}")
    public ResponseEntity<?> findById(@PathVariable Long Id){
        SMSRequestEls result = smsRequestServiceEls.getSMSRequestById(Id);
        return ResponseEntity.ok(result);
    }

    // Sending SMS
    @PostMapping("/send_sms")
    public ResponseEntity<Map<String, Object>> sendSMSEls(@RequestBody SMSRequestEls smsRequestEls) throws ValidationException {
        if(smsRequestEls.getPhoneNumber()==null){
            System.out.println("The phone number is null");
            throw new ValidationException("Phone number is mandatory");
        }

        SMSRequestEls newSMSEls = smsRequestServiceEls.sendSMSEls(smsRequestEls);

        HashMap<String, Object> successResponse = new HashMap<>();
        Map<String, Object> responseData = new HashMap<>();

        responseData.put("requestId", newSMSEls.getId());
        responseData.put("comments", "Successfully Sent");
        successResponse.put("data", responseData);

        return ResponseEntity.ok(successResponse);
    }

    // Search Query to retrieve all messages by a phoneNumber given startTime and endTime.
    @GetMapping("/search")
    public ResponseEntity<?> searchSms(@RequestParam String phoneNumber,
                                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime startTime,
                                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime endTime,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        ElsRequest request = new ElsRequest(phoneNumber, startTime, endTime, page, size);
        Page<SMSRequestEls> response = smsRequestServiceEls.searchSMS(request);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/byText")
    public ResponseEntity<Page<SMSRequestEls>> getSmsContainingText(
            @RequestParam String searchText,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<SMSRequestEls> result = smsRequestServiceEls.getSmsContainingMessage(searchText, PageRequest.of(page, size));
        return ResponseEntity.ok(result);
    }

    // Delete message by ID.
    @DeleteMapping("/delete/{Id}")
    public void deleteSMSEls(@PathVariable Long Id) {
        smsRequestServiceEls.deleteSMSEls(Id);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(ValidationException exc){
        ErrorResponse error = new ErrorResponse();
        error.setMessage(exc.getMessage());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }
}
