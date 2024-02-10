package com.notification.service.v2v.Notifiservice.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(indexName = "sms_index")
public class SMSRequestEls {

    @Id
    private Long id;
    @Getter
    private String phoneNumber;
    @Getter
    private String message;
    @Getter
    private LocalDateTime createdAt;

}
