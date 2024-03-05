package com.notification.service.v2v.Notifiservice.transfomer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;

import com.notification.service.v2v.Notifiservice.transformer.SMSRequestToESEntityTransformer;

import com.notification.service.v2v.Notifiservice.data.entity.ESEntity;
import com.notification.service.v2v.Notifiservice.data.entity.SmsEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SMSRequestToESEntityTransformerTest {

    @Test
    public void testTransformer() {
        // Mock SmsEntity
        SmsEntity smsEntity = mock(SmsEntity.class);
        when(smsEntity.getId()).thenReturn(1L);
        when(smsEntity.getMessage()).thenReturn("Test message");
        when(smsEntity.getPhoneNumber()).thenReturn("1234567890");
        when(smsEntity.getStatus()).thenReturn("SENT");
        when(smsEntity.getCreatedAt()).thenReturn(Timestamp.valueOf("2024-02-24 10:00:00").toLocalDateTime());
        when(smsEntity.getUpdatedAt()).thenReturn(Timestamp.valueOf("2024-02-24 10:00:00").toLocalDateTime());

        // Invoke transformer
        ESEntity esEntity = SMSRequestToESEntityTransformer.transformer(smsEntity);

        // Verify transformed entity
        assertEquals(1L, esEntity.getId());
        assertEquals("Test message", esEntity.getMessage());
        assertEquals("1234567890", esEntity.getPhoneNumber());
        assertEquals("SENT", esEntity.getStatus());
        assertEquals(Timestamp.valueOf("2024-02-24 10:00:00").getTime(), esEntity.getCreatedAt());
        assertEquals(Timestamp.valueOf("2024-02-24 10:00:00").getTime(), esEntity.getUpdatedAt());
        assertNull(esEntity.getFailureCode());
        assertNull(esEntity.getFailureComment());
    }
}
