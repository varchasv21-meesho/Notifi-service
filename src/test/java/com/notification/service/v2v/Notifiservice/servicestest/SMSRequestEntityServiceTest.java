package com.notification.service.v2v.Notifiservice.servicestest;

import com.notification.service.v2v.Notifiservice.dao.SMSRequestRepository;
import com.notification.service.v2v.Notifiservice.entity.PageDetails;
import com.notification.service.v2v.Notifiservice.entity.SMSRequestEntity;
import com.notification.service.v2v.Notifiservice.exceptionHandling.ValidationException;
import com.notification.service.v2v.Notifiservice.kafka.producer.MessageProducer;
import com.notification.service.v2v.Notifiservice.rest.responses.CustomResponse;
import com.notification.service.v2v.Notifiservice.rest.responses.SMSResponse;
import com.notification.service.v2v.Notifiservice.services.SMSRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SMSRequestEntityServiceTest {

    @Mock
    private SMSRequestRepository smsRequestRepository;

    @Mock
    private MessageProducer messageProducer;

    @InjectMocks
    private SMSRequestService smsRequestService;

    @BeforeEach
    void setUp(){
        reset(smsRequestRepository,messageProducer);
    }

    @Test
    public void testAddSMSRequest_invalidPhoneNumber() throws ValidationException {
        SMSRequestEntity smsRequestEntity = new SMSRequestEntity("1234567890","test message");
        ValidationException exception = assertThrows(ValidationException.class, () -> smsRequestService.addSmsRequest(smsRequestEntity));
        verify(smsRequestRepository,never()).save(any(SMSRequestEntity.class));
        verify(messageProducer,never()).sendMessage(any(String.class),any(String.class));
        assertEquals(400, exception.getErrorResponse().getStatus());
        assertEquals("please check phone number", exception.getErrorResponse().getMessage());
    }

    @Test
    public void testGetAllSMSRequests() {
        // Mocking the behavior of repository
        when(smsRequestRepository.findAll()).thenReturn(List.of(
                new SMSRequestEntity("1234567890", "Test message"),
                new SMSRequestEntity("0987654321", "Another test message")
        ));

        List<SMSRequestEntity> allRequests = smsRequestService.getAllSMSRequests().getData();
        assertNotNull(allRequests);
        assertEquals(2, allRequests.size());
    }

    @Test
    public void testGetSmsRequestById() throws ValidationException {
        // Mocking the behavior of repository
        SMSRequestEntity smsRequestEntity = new SMSRequestEntity("1234567890", "Test message");
        smsRequestEntity.setId(1L);
        when(smsRequestRepository.findById(1L)).thenReturn(Optional.of(smsRequestEntity));

        SMSRequestEntity result = smsRequestService.getSmsRequestById(1L).getData();
        assertNotNull(result);
        assertEquals("1234567890", result.getPhoneNumber());
        assertEquals("Test message", result.getMessage());
    }

    @Test
    public void testAddSmsRequest() throws ValidationException {
        SMSRequestEntity smsRequestEntity = new SMSRequestEntity("9876543210", "Test message");

        // Mocking the behavior of repository
        when(smsRequestRepository.save(any(SMSRequestEntity.class))).thenReturn(smsRequestEntity);

        CustomResponse<SMSResponse, String, PageDetails> addedRequest = smsRequestService.addSmsRequest(smsRequestEntity);
        assertNotNull(addedRequest);
        assertEquals("Pending", addedRequest.getData().getComments());
    }

    @Test
    public void testDeleteSmsRequest() throws ValidationException {
        Long requestId = 1L;

        // Mocking the behavior of repository
        when(smsRequestRepository.existsById(requestId)).thenReturn(true);

        // Verify that deleteById method is called once
        smsRequestService.deleteSmsRequest(requestId);
        verify(smsRequestRepository, times(1)).deleteById(requestId);
    }

    @Test
    void testGetSmsRequestById_ValidId_ReturnsCorrectResponse() throws ValidationException {
        long id = 1L;
        SMSRequestEntity mockEntity = new SMSRequestEntity();
        mockEntity.setId(id);
        when(smsRequestRepository.findById(id)).thenReturn(Optional.of(mockEntity));

        assertNotNull(smsRequestService.getSmsRequestById(id));
        assertEquals(id, smsRequestService.getSmsRequestById(id).getData().getId());

        verify(smsRequestRepository, times(2)).findById(id);
    }

    @Test
    void testGetSmsRequestById_InvalidId_ThrowsValidationException() {
        long invalidId = -1L;

        assertThrows(ValidationException.class, () -> smsRequestService.getSmsRequestById(invalidId));

        verify(smsRequestRepository, never()).findById(invalidId);
    }

    @Test
    void testAddSmsRequest_ValidPhoneNumber_SavesAndProducesMessage() throws ValidationException {
        SMSRequestEntity mockEntity = new SMSRequestEntity();
        mockEntity.setPhoneNumber("9876543210");

        when(smsRequestRepository.save(any())).thenReturn(mockEntity);

        assertNotNull(smsRequestService.addSmsRequest(mockEntity));

        verify(smsRequestRepository, times(1)).save(mockEntity);
        verify(messageProducer, times(1)).sendMessage("send_sms", String.valueOf(mockEntity.getId()));
    }

    @Test
    void testAddSmsRequest_InvalidPhoneNumber_ThrowsValidationException() {
        SMSRequestEntity mockEntity = new SMSRequestEntity();
        mockEntity.setPhoneNumber("1234567890");

        assertThrows(ValidationException.class, () -> smsRequestService.addSmsRequest(mockEntity));

        verify(smsRequestRepository, never()).save(mockEntity);
        verify(messageProducer, never()).sendMessage(any(), any());
    }

    @Test
    void testDeleteSmsRequest_ValidId_DeletesAndReturnsCorrectResponse() throws ValidationException {
        long id = 1L;
        SMSRequestEntity mockEntity = new SMSRequestEntity();
        mockEntity.setId(id);
        when(smsRequestRepository.existsById(id)).thenReturn(true);
        when(smsRequestRepository.findById(id)).thenReturn(Optional.of(mockEntity));

        assertNotNull(smsRequestService.deleteSmsRequest(id));

        verify(smsRequestRepository, times(1)).deleteById(id);
        verify(messageProducer, never()).sendMessage(any(), any());
    }

    @Test
    void testDeleteSmsRequest_InvalidId_ThrowsValidationException() {
        long invalidId = -1L;

        assertThrows(ValidationException.class, () -> smsRequestService.deleteSmsRequest(invalidId));

        verify(smsRequestRepository, never()).deleteById(invalidId);
        verify(messageProducer, never()).sendMessage(any(), any());
    }
}
