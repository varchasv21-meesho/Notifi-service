package com.notification.service.v2v.Notifiservice.validators;

import static org.junit.jupiter.api.Assertions.*;
import com.notification.service.v2v.Notifiservice.data.entity.PageDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PageValidatorTest {

    @Test
    public void testCheckPages_NullPageDetails() {
        assertTrue(PageValidator.checkPages(null));
    }

    @Test
    public void testCheckPages_NullPage() {
        PageDetails pageDetails = new PageDetails();
        pageDetails.setSize(10);
        assertTrue(PageValidator.checkPages(pageDetails));
    }

    @Test
    public void testCheckPages_NullSize() {
        PageDetails pageDetails = new PageDetails();
        pageDetails.setPage(1);
        assertTrue(PageValidator.checkPages(pageDetails));
    }

    @Test
    public void testCheckPages_NegativePage() {
        PageDetails pageDetails = new PageDetails();
        pageDetails.setPage(-1);
        pageDetails.setSize(10);
        assertTrue(PageValidator.checkPages(pageDetails));
    }

    @Test
    public void testCheckPages_ZeroSize() {
        PageDetails pageDetails = new PageDetails();
        pageDetails.setPage(1);
        pageDetails.setSize(0);
        assertTrue(PageValidator.checkPages(pageDetails));
    }

    @Test
    public void testCheckPages_ExceedMaxSize() {
        PageDetails pageDetails = new PageDetails();
        pageDetails.setPage(1);
        pageDetails.setSize(55);
        assertTrue(PageValidator.checkPages(pageDetails));
    }

    @Test
    public void testCheckPages_ValidPageDetails() {
        PageDetails pageDetails = new PageDetails();
        pageDetails.setPage(1);
        pageDetails.setSize(10);
        assertFalse(PageValidator.checkPages(pageDetails));
    }
}
