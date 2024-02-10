package com.notification.service.v2v.Notifiservice.dao;

import com.notification.service.v2v.Notifiservice.entity.SMSRequestEls;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SMSRequestRepoEls extends ElasticsearchRepository<SMSRequestEls, Long>, CrudRepository<SMSRequestEls, Long> {

    @Query("{\"bool\": {\"must\": [{\"match\": {\"phoneNumber\": \"?0\"}}, {\"range\": {\"createdAt\": {\"gte\": \"?1\", \"lte\": \"?2\"}}}]}}")
    Page<SMSRequestEls> findByPhoneNumberAndCreatedAtBetweenOrderByCreatedAtDesc(String phoneNumber, LocalDateTime startTime, LocalDateTime endTime, PageRequest pageRequest);

    Page<SMSRequestEls> findByMessageContaining(String searchText, PageRequest pageable);
//    List<SMSRequestEls> findAll();
//
//    SMSRequestEls findById(Long id);
//
//    SMSRequestEls save(SMSRequestEls smsRequestEls);

//    void deleteById(@NotNull Long id);
//
//    Iterable<SMSRequestEls> findAll();
//
//    List<Object> findById(Long id);
//
//    SMSRequestEls save(SMSRequestEls smsRequestEls);
}
