package com.notification.service.v2v.Notifiservice.dao;

import com.notification.service.v2v.Notifiservice.entity.ESEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ESRepository extends ElasticsearchRepository<ESEntity, Long>, CrudRepository<ESEntity, Long> {

    @Query("{\"bool\": {\"must\": [{\"match\": {\"phoneNumber\": \"?0\"}}, {\"range\": {\"createdAt\": {\"gte\": \"?1\", \"lte\": \"?2\"}}}]}}")
    Page<ESEntity> findByPhoneNumberAndCreatedAtBetweenOrderByCreatedAtDesc(String phoneNumber, LocalDateTime startTime, LocalDateTime endTime, PageRequest pageRequest);

    Page<ESEntity> findByMessageContaining(String searchText, PageRequest pageable);

}
