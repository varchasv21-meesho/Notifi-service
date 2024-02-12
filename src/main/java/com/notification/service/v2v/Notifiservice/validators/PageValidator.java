package com.notification.service.v2v.Notifiservice.validators;

import com.notification.service.v2v.Notifiservice.entity.PageDetails;

public class PageValidator {
    public static boolean checkPages(PageDetails pageDetails){
        if (pageDetails == null) return true;
        if (pageDetails.getPage() == null) return true;
        if (pageDetails.getSize() == null) return true;
        if (pageDetails.getPage() < 0) return true;
        if (pageDetails.getSize() < 1) return true;
        return pageDetails.getSize() > 50;
    }
}
