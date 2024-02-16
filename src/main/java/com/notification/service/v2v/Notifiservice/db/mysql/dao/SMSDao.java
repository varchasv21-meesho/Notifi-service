package com.notification.service.v2v.Notifiservice.db.mysql.dao;

import com.notification.service.v2v.Notifiservice.data.entity.SmsEntity;
import com.notification.service.v2v.Notifiservice.db.mysql.repository.SMSRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SMSDao {

    @Autowired
    private final SMSRepository smsRepository;

    public SMSDao(SMSRepository smsRepository) {
        this.smsRepository = smsRepository;
    }

    public SmsEntity save(SmsEntity smsEntity){
        return smsRepository.save(smsEntity);
    }

    public List<SmsEntity> findAll() {
        return smsRepository.findAll();
    }

    public Optional<SmsEntity> findById(Long id) {
        return smsRepository.findById(id);
    }

    public boolean existsById(Long id) {
        return smsRepository.existsById(id);
    }

    public void deleteById(Long id) {
        smsRepository.deleteById(id);
    }
}
