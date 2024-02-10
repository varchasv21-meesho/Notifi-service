package com.notification.service.v2v.Notifiservice.controllers;

import com.notification.service.v2v.Notifiservice.exceptionHandling.ValidationException;
import com.notification.service.v2v.Notifiservice.rest.requests.BlacklistRequest;
import com.notification.service.v2v.Notifiservice.services.BlacklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/v1/blacklist")
public class BlacklistController {

    public BlacklistService blacklistService;

    @Autowired
    BlacklistController(BlacklistService blacklistService1){
        blacklistService = blacklistService1;
    }

    @GetMapping
    public Set<String> getBlacklist() throws ValidationException {
        return blacklistService.getBlacklist();
    }

    @PostMapping
    public ResponseEntity<?> addToBlacklist(@RequestBody BlacklistRequest request) throws ValidationException {
        Set<String> phoneNumbers = request.getPhoneNumbers();
        blacklistService.addToBlacklist(phoneNumbers);
        Map<String, String> response = new HashMap<>();
        response.put("data", "Successfully blacklisted");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<?> removeFromBlacklist(@RequestBody BlacklistRequest request) throws ValidationException {
        Set<String> phoneNumbers = request.getPhoneNumbers();

        // Remove from Redis and MySQL
        blacklistService.removeFromBlacklist(phoneNumbers);
        Map<String, String> response = new HashMap<>();
        response.put("data", "Successfully whitelisted");
        return ResponseEntity.ok(response);
    }
}
