package com.notification.service.v2v.Notifiservice.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(indexName = "sms_index")
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ESEntity {

    @Id
    private Long id;
    @Field(type = FieldType.Text, analyzer = "standard", searchAnalyzer = "standard")
    String phoneNumber;
    @Field(type = FieldType.Text, analyzer = "standard", searchAnalyzer = "standard")
    String message;
    String status;
    String failureCode;
    String failureComment;
    @Field(type = FieldType.Long)
    Long createdAt;
    @Field(type = FieldType.Long)
    Long updatedAt;
}
