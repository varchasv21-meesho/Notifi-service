package com.notification.service.v2v.Notifiservice.dao;

import com.notification.service.v2v.Notifiservice.entity.SMSRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SMSRequestRepository extends JpaRepository<SMSRequestEntity,Long> {
}
