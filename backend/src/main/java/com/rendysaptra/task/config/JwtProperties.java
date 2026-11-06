package com.rendysaptra.task.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    
    @NotBlank(message = "JWT_SECRET must be set")
    @Size(min = 64, message = "JWT_SECRET must be at least 64 characters")
    private String secret;
    
    private long expiration = 86400000;
    
    public String getSecret() {
        return secret;
    }
    
    public void setSecret(String secret) {
        this.secret = secret;
    }
    
    public long getExpiration() {
        return expiration;
    }
    
    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }
}
