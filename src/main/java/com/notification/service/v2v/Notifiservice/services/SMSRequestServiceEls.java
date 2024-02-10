package com.notification.service.v2v.Notifiservice.services;

import com.notification.service.v2v.Notifiservice.dao.SMSRequestRepoEls;
import com.notification.service.v2v.Notifiservice.entity.SMSRequestEls;
import com.notification.service.v2v.Notifiservice.rest.requests.ElsRequest;
//import com.notification.service.v2v.Notifiservice.rest.responses.ElsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;

@Service
public class SMSRequestServiceEls {
    private final SMSRequestRepoEls smsRequestRepoEls;

    @Autowired
    public SMSRequestServiceEls(SMSRequestRepoEls smsRequestRepoEls) {
        this.smsRequestRepoEls = smsRequestRepoEls;
    }

//    public SMSRequestEls save(SMSRequestEls els){
//        try{
//            smsRequestRepoEls.save(els);
//        }catch (Exception e){
//            System.out.println(e.getMessage());
//        }
//
//        return els;
//    }



    public Iterable<SMSRequestEls> getAllSMSRequestsEls(){
        return smsRequestRepoEls.findAll();
    }

    public SMSRequestEls getSMSRequestById(Long Id){
        return smsRequestRepoEls.findById(Id).orElse(null);
    }

    public SMSRequestEls sendSMSEls(SMSRequestEls smsRequestEls){
        smsRequestEls.setCreatedAt(LocalDateTime.now());

        return smsRequestRepoEls.save(smsRequestEls);
    }

    public void deleteSMSEls(Long id){
        smsRequestRepoEls.deleteById(id);
    }

    public Page<SMSRequestEls> searchSMS(ElsRequest request) {
//        Page<SMSRequestEls> smsPage = smsRequestRepoEls.findByPhoneNumberAndCreatedAtBetweenOrderByCreatedAtDesc(
//                request.getPhoneNumber(), request.getStartTime(), request.getEndTime(), PageRequest.of(request.getPage(), request.getSize())
//        );
//
//        List<SMSRequestEls> messages = new ArrayList<>(smsPage.getContent());
//
//        ElsResponse response = new ElsResponse();
//        response.setMessages(messages);
//        response.setTotalPages(smsPage.getTotalPages());
//        return response;

        return smsRequestRepoEls.findByPhoneNumberAndCreatedAtBetweenOrderByCreatedAtDesc(
                request.getPhoneNumber(), request.getStartTime(), request.getEndTime(), PageRequest.of(request.getPage(), request.getSize())
        );
    }

    public Page<SMSRequestEls> getSmsContainingMessage(String searchText, PageRequest pageable) {
        return smsRequestRepoEls.findByMessageContaining(searchText, pageable);
    }
}
