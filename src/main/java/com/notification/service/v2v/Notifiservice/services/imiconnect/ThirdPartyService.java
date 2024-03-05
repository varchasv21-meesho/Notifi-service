package com.notification.service.v2v.Notifiservice.services.imiconnect;

import com.notification.service.v2v.Notifiservice.data.entity.imiconnect.SmsModal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class ThirdPartyService {

    private final SmsBuilder smsBuilder;

    @Value("${notification.service.sms-thirdParty-api-key}")
    private String apiKey;

    public RestTemplate restTemplate = new RestTemplateBuilder()
            .defaultHeader("key",apiKey)
            .defaultHeader("Content-type", MediaType.APPLICATION_JSON_VALUE)
            .build();

    @Value("${notification.service.sms-api-url}")
    private String apiUrl;

    public ThirdPartyService(SmsBuilder smsBuilder) {
        this.smsBuilder = smsBuilder;
    }

    public String makeAPICall(String correlationId, String phoneNumber, String message){
        SmsModal connect;
        try {
            connect = smsBuilder.buildSmsModal(correlationId,phoneNumber,message);
            log.info("SmsModal created: " + connect);
        }catch (Exception e){
            log.info("smsBuilder : buildSmsModal() call threw error");
            throw new RuntimeException(String.valueOf(e));
        }

        try {
            String response = restTemplate.postForObject(apiUrl,connect, String.class);
            log.info(response);
            return response;
        }catch (RuntimeException e) {
            log.error(e.getMessage());
        }
        return "API Execution Failure";
    }
}
