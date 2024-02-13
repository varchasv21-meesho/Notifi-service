package com.notification.service.v2v.Notifiservice.servicestest;

import com.notification.service.v2v.Notifiservice.dao.ESRepository;
import com.notification.service.v2v.Notifiservice.dao.SMSRequestRepository;
import com.notification.service.v2v.Notifiservice.entity.ESEntity;
import com.notification.service.v2v.Notifiservice.entity.PageDetails;
import com.notification.service.v2v.Notifiservice.entity.SMSRequestEntity;
import com.notification.service.v2v.Notifiservice.exceptionHandling.ValidationException;
import com.notification.service.v2v.Notifiservice.rest.requests.ESTextSearchRequest;
import com.notification.service.v2v.Notifiservice.rest.requests.ESTimeRangeRequest;
import com.notification.service.v2v.Notifiservice.rest.responses.CustomResponse;
import com.notification.service.v2v.Notifiservice.services.ESService;
import com.notification.service.v2v.Notifiservice.services.SMSRequestService;
import com.notification.service.v2v.Notifiservice.validators.PageValidator;
import com.notification.service.v2v.Notifiservice.validators.SmsRequestValidator;
import com.notification.service.v2v.Notifiservice.validators.TimeValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.sql.Timestamp;
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
    private SMSRequestRepository smsRequestRepository;

    @Mock
    private SMSRequestService smsRequestService;

    @InjectMocks
    private ESService esService;

    @Test
    public void testSaveValidPhoneNumber() throws ValidationException {
        SMSRequestEntity validEntity = new SMSRequestEntity();
        validEntity.setPhoneNumber("9234567890");
        validEntity.setCreatedAt(LocalDateTime.now());
        validEntity.setUpdatedAt(LocalDateTime.now());

        when(smsRequestRepository.save(any())).thenReturn(validEntity);
        when(smsRequestService.setDetails(any())).thenReturn(validEntity);

        assertDoesNotThrow(() -> esService.save(validEntity));

        verify(esRepository, times(1)).save(any(ESEntity.class));
    }

    @Test
    public void testSaveInvalidPhoneNumber() {
        SMSRequestEntity invalidEntity = new SMSRequestEntity();
        invalidEntity.setPhoneNumber("invalid");

        ValidationException exception = assertThrows(ValidationException.class, () -> esService.save(invalidEntity));
        assertEquals("Please enter valid phone number", exception.getMessage());

        verify(smsRequestRepository, never()).save(any());
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

        CustomResponse<List<ESEntity>, String, PageDetails> response = esService.findByPhoneNumberAndTimeRange(request);

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

        CustomResponse<List<ESEntity>, String, PageDetails> response = esService.findByMessage(request);

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

        CustomResponse<List<ESEntity>, String, PageDetails> response = esService.findAll(pageDetails);

        assertNotNull(response);
        assertEquals(entities, response.getData());

        verify(esRepository, times(1)).findAll((Pageable) any());
    }
}
