package com.notification.service.v2v.Notifiservice.db.elasticsearch.dao;

import com.notification.service.v2v.Notifiservice.data.entity.ESEntity;
import com.notification.service.v2v.Notifiservice.db.elasticsearch.repository.ESRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class ESDao {

    @Autowired
    private final ESRepository esRepository;

    public ESDao(ESRepository esRepository) {
        this.esRepository = esRepository;
    }

    public ESEntity save(ESEntity esEntity){
        return esRepository.save(esEntity);
    }

    public Page<ESEntity> findByPhoneNumberAndCreatedAtBetween(String phoneNumber, Long startTime, Long endTime, Pageable pageable){
        return esRepository.findByPhoneNumberAndCreatedAtBetween(phoneNumber,startTime,endTime,pageable);
    }

    public Page<ESEntity> findByMessageContaining(String searchText, PageRequest pageable){
        return esRepository.findByMessageContaining(searchText,pageable);
    }

    public Page<ESEntity> findAll(PageRequest pageRequest) {
        return esRepository.findAll(pageRequest);
    }
}
