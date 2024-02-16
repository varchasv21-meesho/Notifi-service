package com.notification.service.v2v.Notifiservice.services;

import com.notification.service.v2v.Notifiservice.data.entity.ESEntity;
import com.notification.service.v2v.Notifiservice.data.entity.PageDetails;
import com.notification.service.v2v.Notifiservice.data.entity.SmsEntity;
import com.notification.service.v2v.Notifiservice.data.requests.ESTextSearchRequest;
import com.notification.service.v2v.Notifiservice.data.requests.ESTimeRangeRequest;
import com.notification.service.v2v.Notifiservice.data.responses.CustomResponse;
import com.notification.service.v2v.Notifiservice.data.responses.SMSResponse;
import com.notification.service.v2v.Notifiservice.db.elasticsearch.dao.ESDao;
import com.notification.service.v2v.Notifiservice.db.mysql.dao.SMSDao;
import com.notification.service.v2v.Notifiservice.exceptionHandling.ValidationException;
import com.notification.service.v2v.Notifiservice.kafka.producer.MessageProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SmsServiceTest {

    @Mock
    private SMSDao smsDao;

    @Mock
    private ESDao esDao;
    @Mock
    private MessageProducer messageProducer;

    @Mock
    private BlacklistService blacklistService;

    @InjectMocks
    private SmsService smsService;

    @BeforeEach
    void setUp(){
        reset(smsDao,esDao,blacklistService,messageProducer);
    }

    @Test
    public void testAddSMSRequest_invalidPhoneNumber() throws ValidationException {
        SmsEntity smsEntity = new SmsEntity("1234567890","test message");
        ValidationException exception = assertThrows(ValidationException.class, () -> smsService.addSms(smsEntity));
        verify(smsDao,never()).save(any(SmsEntity.class));
        verify(messageProducer,never()).sendMessage(any(String.class),any(String.class));
        assertEquals(400, exception.getErrorResponse().getStatus());
        assertEquals("please check phone number", exception.getErrorResponse().getMessage());
    }

    @Test
    public void testGetAllSMSRequests() {
        // Mocking the behavior of repository
        when(smsDao.findAll()).thenReturn(List.of(
                new SmsEntity("1234567890", "Test message"),
                new SmsEntity("0987654321", "Another test message")
        ));

        List<SmsEntity> allRequests = smsService.getAllSMSRequests().getData();
        assertNotNull(allRequests);
        assertEquals(2, allRequests.size());
    }

    @Test
    public void testGetSmsRequestById() throws ValidationException {
        // Mocking the behavior of repository
        SmsEntity smsEntity = new SmsEntity("1234567890", "Test message");
        smsEntity.setId(1L);
        when(smsDao.findById(1L)).thenReturn(Optional.of(smsEntity));

        SmsEntity result = smsService.getSmsRequestById(1L).getData();
        assertNotNull(result);
        assertEquals("1234567890", result.getPhoneNumber());
        assertEquals("Test message", result.getMessage());
    }

    @Test
    public void testAddSmsRequest() throws ValidationException {
        SmsEntity smsEntity = new SmsEntity("9876543210", "Test message");
        smsEntity.setId(1L);
        // Mocking the behavior of repository
        when(smsDao.save(any(SmsEntity.class))).thenReturn(smsEntity);

        CustomResponse<SMSResponse, String, PageDetails> addedRequest = smsService.addSms(smsEntity);
        assertNotNull(addedRequest);
        assertEquals("Pending", addedRequest.getData().getComments());
    }

    @Test
    public void testDeleteSmsRequest() throws ValidationException {
        Long requestId = 1L;

        // Mocking the behavior of repository
        when(smsDao.existsById(requestId)).thenReturn(true);

        // Verify that deleteById method is called once
        smsService.deleteSmsRequest(requestId);
        verify(smsDao, times(1)).deleteById(requestId);
    }

    @Test
    void testGetSmsRequestById_ValidId_ReturnsCorrectResponse() throws ValidationException {
        long id = 1L;
        SmsEntity mockEntity = new SmsEntity();
        mockEntity.setId(id);
        when(smsDao.findById(id)).thenReturn(Optional.of(mockEntity));

        assertNotNull(smsService.getSmsRequestById(id));
        assertEquals(id, smsService.getSmsRequestById(id).getData().getId());

        verify(smsDao, times(2)).findById(id);
    }

    @Test
    void testGetSmsRequestById_InvalidId_ThrowsValidationException() {
        long invalidId = -1L;

        assertThrows(ValidationException.class, () -> smsService.getSmsRequestById(invalidId));

        verify(smsDao, never()).findById(invalidId);
    }

    @Test
    void testAddSmsRequest_ValidPhoneNumber_SavesAndProducesMessage() throws ValidationException {
        SmsEntity mockEntity = new SmsEntity();
        mockEntity.setPhoneNumber("9876543210");

        when(smsDao.save(any())).thenReturn(mockEntity);

        assertNotNull(smsService.addSms(mockEntity));

        verify(smsDao, times(1)).save(mockEntity);
        verify(messageProducer, times(1)).sendMessage("send_sms", String.valueOf(mockEntity.getId()));
    }

    @Test
    void testAddSmsRequest_InvalidPhoneNumber_ThrowsValidationException() {
        SmsEntity mockEntity = new SmsEntity();
        mockEntity.setPhoneNumber("1234567890");

        assertThrows(ValidationException.class, () -> smsService.addSms(mockEntity));

        verify(smsDao, never()).save(mockEntity);
        verify(messageProducer, never()).sendMessage(any(), any());
    }

    @Test
    void testDeleteSmsRequest_ValidId_DeletesAndReturnsCorrectResponse() throws ValidationException {
        long id = 1L;
        SmsEntity mockEntity = new SmsEntity();
        mockEntity.setId(id);
        when(smsDao.existsById(id)).thenReturn(true);
        when(smsDao.findById(id)).thenReturn(Optional.of(mockEntity));

        assertNotNull(smsService.deleteSmsRequest(id));

        verify(smsDao, times(1)).deleteById(id);
        verify(messageProducer, never()).sendMessage(any(), any());
    }

    @Test
    void testDeleteSmsRequest_InvalidId_ThrowsValidationException() {
        long invalidId = -1L;

        assertThrows(ValidationException.class, () -> smsService.deleteSmsRequest(invalidId));

        verify(smsDao, never()).deleteById(invalidId);
        verify(messageProducer, never()).sendMessage(any(), any());
    }

    // Elasticsearch tests


    @Test
    public void testSaveValidPhoneNumber() throws ValidationException {
        SmsEntity validEntity = new SmsEntity();
        validEntity.setPhoneNumber("9234567890");
        validEntity.setCreatedAt(LocalDateTime.now());
        validEntity.setUpdatedAt(LocalDateTime.now());

        when(smsDao.save(any())).thenReturn(validEntity);
        when(blacklistService.isBlacklisted(anyString())).thenReturn(Boolean.FALSE);

        SmsEntity returnedEntity = smsService.setDetails(validEntity);

        assertSame(validEntity, returnedEntity);
        assertDoesNotThrow(() -> smsService.addSms(validEntity));

        verify(esDao, times(1)).save(any(ESEntity.class));
    }

    @Test
    public void testSaveInvalidPhoneNumber() {
        SmsEntity invalidEntity = new SmsEntity();
        invalidEntity.setPhoneNumber("invalid");

        ValidationException exception = assertThrows(ValidationException.class, () -> smsService.addSms(invalidEntity));
        assertEquals("please check phone number", exception.getErrorResponse().getMessage());

        verify(smsDao, never()).save(any());
        verify(esDao, never()).save(any());
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

        when(esDao.findByPhoneNumberAndCreatedAtBetween(anyString(), anyLong(), anyLong(), any())).thenReturn(page);

        CustomResponse<List<ESEntity>, String, PageDetails> response = smsService.findByPhoneNumberAndTimeRange(request);

        assertNotNull(response);
        assertEquals(entities, response.getData());

        verify(esDao, times(1)).findByPhoneNumberAndCreatedAtBetween(anyString(), anyLong(), anyLong(), any());
    }

    @Test
    public void testFindByMessage() throws ValidationException {
        ESTextSearchRequest request = new ESTextSearchRequest();
        request.setText("keyword");
        request.setPageDetails(new PageDetails(0, 10));

        List<ESEntity> entities = new ArrayList<>();
        Page<ESEntity> page = new PageImpl<>(entities);

        when(esDao.findByMessageContaining(anyString(), any())).thenReturn(page);

        CustomResponse<List<ESEntity>, String, PageDetails> response = smsService.findByMessage(request);

        assertNotNull(response);
        assertEquals(entities, response.getData());

        verify(esDao, times(1)).findByMessageContaining(anyString(), any());
    }

    @Test
    public void testFindAll() throws ValidationException {
        PageDetails pageDetails = new PageDetails(1, 10);

        List<ESEntity> entities = new ArrayList<>();
        Page<ESEntity> page = new PageImpl<>(entities);

//        when(PageValidator.checkPages(any())).thenReturn(false);
        when(esDao.findAll(any())).thenReturn(page);

        CustomResponse<List<ESEntity>, String, PageDetails> response = smsService.findAll(pageDetails);

        assertNotNull(response);
        assertEquals(entities, response.getData());

        verify(esDao, times(1)).findAll(any());
    }
}
