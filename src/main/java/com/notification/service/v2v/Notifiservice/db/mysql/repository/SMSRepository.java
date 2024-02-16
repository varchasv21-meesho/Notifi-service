package com.notification.service.v2v.Notifiservice.db.mysql.repository;

import com.notification.service.v2v.Notifiservice.data.entity.SmsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SMSRepository extends JpaRepository<SmsEntity,Long> {
}
