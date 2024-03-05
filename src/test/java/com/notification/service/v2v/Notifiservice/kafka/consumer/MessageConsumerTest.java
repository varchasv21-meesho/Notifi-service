package com.notification.service.v2v.Notifiservice.kafka.consumer;

import com.notification.service.v2v.Notifiservice.data.entity.PageDetails;
import com.notification.service.v2v.Notifiservice.data.entity.SmsEntity;
import com.notification.service.v2v.Notifiservice.data.responses.CustomResponse;
import com.notification.service.v2v.Notifiservice.db.mysql.dao.SMSDao;
import com.notification.service.v2v.Notifiservice.exceptionHandling.ErrorResponse;
import com.notification.service.v2v.Notifiservice.services.BlacklistService;
import com.notification.service.v2v.Notifiservice.services.SmsService;
import com.notification.service.v2v.Notifiservice.services.imiconnect.ThirdPartyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageConsumerTest {

    @Mock
    private SmsService smsService;

    @Mock
    private BlacklistService blacklistService;

    @Mock
    private SMSDao smsDao;

    @Mock
    private ThirdPartyService thirdPartyService;

    @InjectMocks
    private MessageConsumer messageConsumer;

    @BeforeEach
    public void setup() {
        reset(smsDao,smsService,blacklistService,thirdPartyService);
    }

    @Test
    void testListen_Blacklisted() {
        String id = "1";
        SmsEntity smsEntity = new SmsEntity();
        smsEntity.setId(1L);
        smsEntity.setPhoneNumber("9876543210");
        CustomResponse<SmsEntity, String, PageDetails> customResponse = new CustomResponse<>(smsEntity,null,null);
        when(smsService.getSmsRequestById(1L)).thenReturn(customResponse);
        when(blacklistService.isBlacklisted(anyString())).thenReturn(true);

        messageConsumer.listen(id);

        verify(smsDao).save(smsEntity);
    }

    @Test
    void testListen_NotBlacklisted() {
        String id = "1";
        SmsEntity smsEntity = new SmsEntity();
        smsEntity.setId(1L);
        smsEntity.setPhoneNumber("9876543210");
        smsEntity.setMessage("Random Message");
        CustomResponse<SmsEntity, String, PageDetails> customResponse = new CustomResponse<>(smsEntity,null,null);
        when(smsService.getSmsRequestById(1L)).thenReturn(customResponse);
        when(blacklistService.isBlacklisted(anyString())).thenReturn(false);
        when(thirdPartyService.makeAPICall(anyString(), anyString(), anyString())).thenReturn("Success");

        messageConsumer.listen(id);

        verify(smsDao).save(smsEntity);
    }

    @Test
    void testListen_ExceptionThrown() {
        String id = "1";
        when(smsService.getSmsRequestById(anyLong())).thenThrow(new NullPointerException("Test Exception"));

        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            messageConsumer.listen(id);
        });

        assertEquals("java.lang.NullPointerException: Test Exception", exception.getMessage());
    }

    @Test
    void testHandleException() {
        RuntimeException exc = new RuntimeException("Validation Exception");
        ResponseEntity<ErrorResponse> responseEntity = messageConsumer.handleException(exc);

        assertEquals("Validation Exception", Objects.requireNonNull(responseEntity.getBody()).getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }
}
