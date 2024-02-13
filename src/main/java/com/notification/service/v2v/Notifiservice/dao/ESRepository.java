package com.notification.service.v2v.Notifiservice.dao;

import com.notification.service.v2v.Notifiservice.entity.ESEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ESRepository extends ElasticsearchRepository<ESEntity, Long> {


    Page<ESEntity> findByPhoneNumberAndCreatedAtBetween(String phoneNumber, Long startTime, Long endTime, Pageable pageable);

    @Query("{\"match\": {\"message\": {\"query\": \"?0\"}}}")
    Page<ESEntity> findByMessageContaining(String searchText, PageRequest pageable);

}
