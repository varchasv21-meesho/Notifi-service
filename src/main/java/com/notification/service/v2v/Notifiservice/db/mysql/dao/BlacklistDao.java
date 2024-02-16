package com.notification.service.v2v.Notifiservice.db.mysql.dao;

import com.notification.service.v2v.Notifiservice.data.entity.BlacklistEntity;
import com.notification.service.v2v.Notifiservice.db.mysql.repository.BlacklistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BlacklistDao {
    @Autowired
    private BlacklistRepository blacklistRepository;

    public void save(BlacklistEntity blacklistEntity){
        blacklistRepository.save(blacklistEntity);
    }

    public void deleteByPhoneNumber(String phoneNumber){
        blacklistRepository.deleteByPhoneNumber(phoneNumber);
    }
}
