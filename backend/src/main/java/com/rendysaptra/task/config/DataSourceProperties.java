package com.rendysaptra.task.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "spring.datasource")
public class DataSourceProperties {
    
    @NotBlank(message = "DB_USER must be set")
    private String username;
    
    @NotBlank(message = "DB_PASSWORD must be set")
    private String password;
    
    @NotBlank(message = "SPRING_DATASOURCE_URL must be set")
    private String url;
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
}
