package com.notification.service.v2v.Notifiservice.services;

import com.notification.service.v2v.Notifiservice.db.elasticsearch.repository.ESRepository;
import com.notification.service.v2v.Notifiservice.db.mysql.repository.SMSRepository;
import com.notification.service.v2v.Notifiservice.data.entity.ESEntity;
import com.notification.service.v2v.Notifiservice.data.entity.PageDetails;
import com.notification.service.v2v.Notifiservice.data.entity.SmsEntity;
import com.notification.service.v2v.Notifiservice.exceptionHandling.ValidationException;
import com.notification.service.v2v.Notifiservice.data.requests.ESTextSearchRequest;
import com.notification.service.v2v.Notifiservice.data.requests.ESTimeRangeRequest;
import com.notification.service.v2v.Notifiservice.data.responses.CustomResponse;
import com.notification.service.v2v.Notifiservice.validators.PageValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ESServiceTests {

    @Mock
    private ESRepository esRepository;

    @Mock
    private SMSRepository smsRepository;

    @InjectMocks
    private SmsService smsService;

    @Test
    public void testSaveValidPhoneNumber() throws ValidationException {
        SmsEntity validEntity = new SmsEntity();
        validEntity.setPhoneNumber("9234567890");
        validEntity.setCreatedAt(LocalDateTime.now());
        validEntity.setUpdatedAt(LocalDateTime.now());

        when(smsRepository.save(any())).thenReturn(validEntity);
        when(smsService.setDetails(any())).thenReturn(validEntity);

        assertDoesNotThrow(() -> smsService.addSms(validEntity));

        verify(esRepository, times(1)).save(any(ESEntity.class));
    }

    @Test
    public void testSaveInvalidPhoneNumber() {
        SmsEntity invalidEntity = new SmsEntity();
        invalidEntity.setPhoneNumber("invalid");

        ValidationException exception = assertThrows(ValidationException.class, () -> smsService.addSms(invalidEntity));
        assertEquals("Please enter valid phone number", exception.getMessage());

        verify(smsRepository, never()).save(any());
        verify(esRepository, never()).save(any());
    }

    @Test
    public void testFindByPhoneNumberAndTimeRange() throws ValidationException {
        ESTimeRangeRequest request = new ESTimeRangeRequest();
        request.setPhoneNumber("9234567890");
        request.setStartTime(LocalDateTime.parse("2024-01-01T00:00:00"));
        request.setEndTime(LocalDateTime.parse("2024-01-31T23:59:59"));
        request.setPageDetails(new PageDetails(0, 10));

        List<ESEntity> entities = new ArrayList<>();
        Page<ESEntity> page = new PageImpl<>(entities);

        when(esRepository.findByPhoneNumberAndCreatedAtBetween(anyString(), anyLong(), anyLong(), any())).thenReturn(page);

        CustomResponse<List<ESEntity>, String, PageDetails> response = smsService.findByPhoneNumberAndTimeRange(request);

        assertNotNull(response);
        assertEquals(entities, response.getData());

        verify(esRepository, times(1)).findByPhoneNumberAndCreatedAtBetween(anyString(), anyLong(), anyLong(), any());
    }

    @Test
    public void testFindByMessage() throws ValidationException {
        ESTextSearchRequest request = new ESTextSearchRequest();
        request.setText("keyword");
        request.setPageDetails(new PageDetails(0, 10));

        List<ESEntity> entities = new ArrayList<>();
        Page<ESEntity> page = new PageImpl<>(entities);

        when(PageValidator.checkPages(any())).thenReturn(false);
        when(esRepository.findByMessageContaining(anyString(), any())).thenReturn(page);

        CustomResponse<List<ESEntity>, String, PageDetails> response = smsService.findByMessage(request);

        assertNotNull(response);
        assertEquals(entities, response.getData());

        verify(esRepository, times(1)).findByMessageContaining(anyString(), any());
    }

    @Test
    public void testFindAll() throws ValidationException {
        PageDetails pageDetails = new PageDetails(1, 10);

        List<ESEntity> entities = new ArrayList<>();
        Page<ESEntity> page = new PageImpl<>(entities);

//        when(PageValidator.checkPages(any())).thenReturn(false);
        when(esRepository.findAll((Pageable) any())).thenReturn(page);

        CustomResponse<List<ESEntity>, String, PageDetails> response = smsService.findAll(pageDetails);

        assertNotNull(response);
        assertEquals(entities, response.getData());

        verify(esRepository, times(1)).findAll((Pageable) any());
    }
}
