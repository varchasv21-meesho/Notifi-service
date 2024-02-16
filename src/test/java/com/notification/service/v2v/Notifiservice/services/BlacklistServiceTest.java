package com.notification.service.v2v.Notifiservice.services;

import com.notification.service.v2v.Notifiservice.db.mysql.dao.BlacklistDao;
import com.notification.service.v2v.Notifiservice.db.mysql.repository.BlacklistRepository;
import com.notification.service.v2v.Notifiservice.data.entity.BlacklistEntity;
import com.notification.service.v2v.Notifiservice.exceptionHandling.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BlacklistServiceTest {

    @Mock
    private BlacklistDao blacklistDao;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @InjectMocks
    private BlacklistService blacklistService;

    @Mock
    private SetOperations<String, String> setOperations;

    @Test
    public void addToBlacklist_ValidNumbers_Success() throws ValidationException {
        Set<String> phoneNumbers = new HashSet<>();
        phoneNumbers.add("8765432109");
        phoneNumbers.add("9876543210");

        when(redisTemplate.opsForSet()).thenReturn(setOperations);

        assertDoesNotThrow(() -> blacklistService.addToBlacklist(phoneNumbers));

        verify(setOperations, times(1)).add(eq("blacklist"), any(String.class));
        verify(blacklistDao, times(2)).save(any(BlacklistEntity.class));
        verify(redisTemplate, times(1)).opsForSet();
        verifyNoMoreInteractions(redisTemplate);
    }

    @Test
    public void addToBlacklist_InvalidNumbers_ValidationException() {
        Set<String> phoneNumbers = new HashSet<>();
        phoneNumbers.add("1234567890");
        phoneNumbers.add("+911876543200");

        assertThrows(ValidationException.class, () -> blacklistService.addToBlacklist(phoneNumbers));

        verify(redisTemplate, never()).opsForSet();
        verify(blacklistDao, never()).save(any(BlacklistEntity.class));
    }

    @Test
    public void addToBlacklist_EmptyList_ValidationException() throws ValidationException {
        Set<String> phoneNumbers = new HashSet<>();

        assertThrows(ValidationException.class,() -> blacklistService.addToBlacklist(phoneNumbers));

        verify(redisTemplate, never()).opsForSet();
        verify(blacklistDao, never()).save(any(BlacklistEntity.class));
    }

    @Test
    public void removeFromBlacklist_ValidNumbers_Success() throws ValidationException {
        Set<String> phoneNumbers = new HashSet<>();
        phoneNumbers.add("8123456790");
        phoneNumbers.add("9876543210");

        when(redisTemplate.opsForSet()).thenReturn(setOperations);

        assertDoesNotThrow(() -> blacklistService.removeFromBlacklist(phoneNumbers));

        verify(setOperations, times(1)).remove(eq("blacklist"), any(String.class));
        verify(blacklistDao, times(2)).deleteByPhoneNumber(anyString());
        verify(redisTemplate, times(1)).opsForSet();
        verifyNoMoreInteractions(redisTemplate);
    }

    @Test
    public void removeFromBlacklist_EmptyList_ValidationException() throws ValidationException {
        Set<String> phoneNumbers = new HashSet<>();

        assertThrows(ValidationException.class,() -> blacklistService.removeFromBlacklist(phoneNumbers));

        verify(redisTemplate, never()).opsForSet();
        verify(blacklistDao, never()).deleteByPhoneNumber(anyString());
    }

    @Test
    public void getBlacklist_ValidNumbers_Success() throws ValidationException {
        Set<String> expectedBlacklist = new HashSet<>();
        expectedBlacklist.add("8012345678");
        expectedBlacklist.add("9876543210");

        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(setOperations.members("blacklist")).thenReturn(expectedBlacklist);

        Set<String> actualBlacklist = blacklistService.getBlacklist();

        assertEquals(expectedBlacklist, actualBlacklist);
    }

    @Test
    public void isBlacklisted_BlacklistedNumber_ReturnsTrue() {
        String phoneNumber = "1234567890";
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(setOperations.isMember("blacklist", phoneNumber)).thenReturn(Boolean.TRUE);

        assertTrue(blacklistService.isBlacklisted(phoneNumber));
    }

    @Test
    public void isBlacklisted_NonBlacklistedNumber_ReturnsFalse() {
        String phoneNumber = "1234567890";
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(setOperations.isMember("blacklist", phoneNumber)).thenReturn(Boolean.FALSE);

        assertFalse(blacklistService.isBlacklisted(phoneNumber));
    }


}
