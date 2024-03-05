package com.notification.service.v2v.Notifiservice.validators;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class SmsRequestValidatorTest {

    @Test
    public void testIsInvalidPhoneNumber_NullPhoneNumber() {
        assertTrue(SmsRequestValidator.isInvalidPhoneNumber(null));
    }

    @Test
    public void testIsInvalidPhoneNumber_EmptyPhoneNumber() {
        assertTrue(SmsRequestValidator.isInvalidPhoneNumber(""));
    }

    @Test
    public void testIsInvalidPhoneNumber_InvalidPhoneNumber() {
        assertTrue(SmsRequestValidator.isInvalidPhoneNumber("+911234567890")); // Incorrect country code
        assertTrue(SmsRequestValidator.isInvalidPhoneNumber("123456789")); // Less than 10 digits
        assertTrue(SmsRequestValidator.isInvalidPhoneNumber("12345678901")); // More than 10 digits
        assertTrue(SmsRequestValidator.isInvalidPhoneNumber("ABCD123456")); // Non-numeric characters
    }

    @Test
    public void testIsInvalidPhoneNumber_ValidPhoneNumber() {
        assertFalse(SmsRequestValidator.isInvalidPhoneNumber("+919876543210"));
        assertFalse(SmsRequestValidator.isInvalidPhoneNumber("9876543210"));
    }

    @Test
    public void testCheckAllPhoneNumber_NullSet() {
        assertTrue(SmsRequestValidator.checkAllPhoneNumber(null));
    }

    @Test
    public void testCheckAllPhoneNumber_EmptySet() {
        assertTrue(SmsRequestValidator.checkAllPhoneNumber(new HashSet<>()));
    }

    @Test
    public void testCheckAllPhoneNumber_InvalidNumbersInSet() {
        Set<String> phoneNumbers = new HashSet<>();
        phoneNumbers.add("+911234567890");
        phoneNumbers.add("123456789");
        assertTrue(SmsRequestValidator.checkAllPhoneNumber(phoneNumbers));
    }

    @Test
    public void testCheckAllPhoneNumber_ValidNumbersInSet() {
        Set<String> phoneNumbers = new HashSet<>();
        phoneNumbers.add("+919876543210");
        phoneNumbers.add("9876543210");
        assertFalse(SmsRequestValidator.checkAllPhoneNumber(phoneNumbers));
    }
}
