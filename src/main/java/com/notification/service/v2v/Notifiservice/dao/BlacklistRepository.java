package com.notification.service.v2v.Notifiservice.dao;

import com.notification.service.v2v.Notifiservice.entity.BlacklistEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlacklistRepository extends JpaRepository<BlacklistEntity, Long> {
    void deleteByPhoneNumber(String phoneNumber);
}
