package com.rendysaptra.task.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StartupValidator {
    
    @Value("${spring.datasource.username:}")
    private String dbUser;
    
    @Value("${spring.datasource.password:}")
    private String dbPassword;
    
    @Value("${spring.datasource.url:}")
    private String dbUrl;
    
    @PostConstruct
    public void validate() {
        validateNotBlank(dbUser, "DB_USER");
        validateNotBlank(dbPassword, "DB_PASSWORD");
        validateNotBlank(dbUrl, "SPRING_DATASOURCE_URL");
    }
    
    private void validateNotBlank(String value, String name) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException("FATAL: " + name + " must be set and not empty");
        }
    }
}
