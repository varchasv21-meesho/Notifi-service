package com.notification.service.v2v.Notifiservice.servicestest;

import com.notification.service.v2v.Notifiservice.dao.BlacklistRepository;
import com.notification.service.v2v.Notifiservice.entity.BlacklistEntity;
import com.notification.service.v2v.Notifiservice.exceptionHandling.ValidationException;
import com.notification.service.v2v.Notifiservice.services.BlacklistService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BlacklistEntityServiceTest {

    @Mock
    private BlacklistRepository blacklistRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @InjectMocks
    private BlacklistService blacklistService;

    @Test
    public void testGetBlacklist() throws ValidationException {
        Set<String> mockBlacklist = new HashSet<>();
        mockBlacklist.add("1234567890");
        mockBlacklist.add("0987654321");

        // Mocking the behavior of redisTemplate
        when(redisTemplate.opsForSet().members("blacklist")).thenReturn(mockBlacklist);

        Set<String> result = blacklistService.getBlacklist();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("1234567890"));
        assertTrue(result.contains("0987654321"));
    }

    @Test
    public void testAddToBlacklist() throws ValidationException {
        Set<String> phoneNumbers = new HashSet<>();
        phoneNumbers.add("1234567890");
        phoneNumbers.add("0987654321");

        // Mocking the behavior of repository
        doNothing().when(blacklistRepository).save(any(BlacklistEntity.class));
//        doNothing().when(redisTemplate.opsForSet()).add(eq("blacklist"), any(String[].class));

        blacklistService.addToBlacklist(phoneNumbers);

        // Verify that save method is called twice
        verify(blacklistRepository, times(2)).save(any(BlacklistEntity.class));
    }

    @Test
    public void testRemoveFromBlacklist() throws ValidationException {
        Set<String> phoneNumbers = new HashSet<>();
        phoneNumbers.add("1234567890");
        phoneNumbers.add("0987654321");

        // Mocking the behavior of repository
        doNothing().when(blacklistRepository).deleteByPhoneNumber(anyString());
//        doNothing().when(redisTemplate.opsForSet()).remove(eq("blacklist"), any(String[].class));

        blacklistService.removeFromBlacklist(phoneNumbers);

        // Verify that deleteByPhoneNumber method is called twice
        verify(blacklistRepository, times(2)).deleteByPhoneNumber(anyString());
    }

    @Test
    public void testIsBlacklisted() {
        String blacklistedNumber = "1234567890";

        // Mocking the behavior of redisTemplate
//        when(redisTemplate.opsForSet().isMember("blacklist", blacklistedNumber)).thenReturn(true);

        assertTrue(blacklistService.isBlacklisted(blacklistedNumber));
    }
}
