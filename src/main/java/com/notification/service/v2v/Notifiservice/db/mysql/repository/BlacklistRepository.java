package com.notification.service.v2v.Notifiservice.db.mysql.repository;

import com.notification.service.v2v.Notifiservice.data.entity.BlacklistEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlacklistRepository extends JpaRepository<BlacklistEntity, Long> {
    void deleteByPhoneNumber(String phoneNumber);
}
