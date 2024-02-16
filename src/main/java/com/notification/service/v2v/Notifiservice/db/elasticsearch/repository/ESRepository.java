package com.notification.service.v2v.Notifiservice.db.elasticsearch.repository;

import com.notification.service.v2v.Notifiservice.data.entity.ESEntity;
import com.notification.service.v2v.Notifiservice.data.entity.SmsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ESRepository extends ElasticsearchRepository<ESEntity, Long> {

    Page<ESEntity> findByPhoneNumberAndCreatedAtBetween(String phoneNumber, Long startTime, Long endTime, Pageable pageable);

    @Query("{\"match\": {\"message\": {\"query\": \"?0\"}}}")
    Page<ESEntity> findByMessageContaining(String searchText, PageRequest pageable);

}
