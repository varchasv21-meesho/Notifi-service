package com.notification.service.v2v.Notifiservice.validators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


import com.notification.service.v2v.Notifiservice.data.requests.ESTimeRangeRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class TimeValidatorTest {

    @Test
    public void testCheckTimings_StartTimeAfterEndTime() {
        // Mock ESTimeRangeRequest with end time before start time
        ESTimeRangeRequest timeRangeRequest = mock(ESTimeRangeRequest.class);
        LocalDateTime endTime = LocalDateTime.of(2024, 2, 24, 10, 0); // End time before start time
        LocalDateTime startTime = LocalDateTime.of(2024, 2, 24, 12, 0);
        when(timeRangeRequest.getEndTime()).thenReturn(endTime);
        when(timeRangeRequest.getStartTime()).thenReturn(startTime);

        // Invoke checkTimings() and assert
        assertTrue(TimeValidator.checkEndTimeIsBeforeStartTime(timeRangeRequest));
    }

    @Test
    public void testCheckTimings_StartTimeBeforeEndTime() {
        // Mock ESTimeRangeRequest with end time after start time
        ESTimeRangeRequest timeRangeRequest = mock(ESTimeRangeRequest.class);
        LocalDateTime startTime = LocalDateTime.of(2024, 2, 24, 10, 0); // Start time before end time
        LocalDateTime endTime = LocalDateTime.of(2024, 2, 24, 12, 0);
        when(timeRangeRequest.getEndTime()).thenReturn(endTime);
        when(timeRangeRequest.getStartTime()).thenReturn(startTime);

        // Invoke checkTimings() and assert
        assertFalse(TimeValidator.checkEndTimeIsBeforeStartTime(timeRangeRequest));
    }
}
