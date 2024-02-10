package com.notification.service.v2v.Notifiservice.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
//import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.notification.service.v2v.Notifiservice.dao")
@ComponentScan(basePackages = { "com.notification.service.v2v.Notifiservice.services" })
public class ElsConfig extends AbstractElasticsearchConfiguration {

    @Bean
    @Override
    public @NotNull RestHighLevelClient elasticsearchClient() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo("localhost:9200")
                .build();

        return RestClients.create(clientConfiguration)
                .rest();
    }

//    @Override
//    public @NotNull ClientConfiguration clientConfiguration(){
//        return ClientConfiguration.builder()
//                .connectedTo("localhost:9092")
//                .build();
//    }
}
