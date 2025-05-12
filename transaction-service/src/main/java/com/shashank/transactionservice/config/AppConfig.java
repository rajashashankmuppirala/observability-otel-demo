package com.shashank.transactionservice.config;

import com.shashank.transactionservice.interceptor.RestTemplateInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
public class AppConfig {

    private final RestTemplateInterceptor restTemplateInterceptor;

    @Autowired
    public AppConfig(RestTemplateInterceptor restTemplateInterceptor) {
        this.restTemplateInterceptor = restTemplateInterceptor;
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        // Add the interceptor to propagate trace headers
        restTemplate.setInterceptors(Collections.singletonList(restTemplateInterceptor));
        return restTemplate;
    }
}
