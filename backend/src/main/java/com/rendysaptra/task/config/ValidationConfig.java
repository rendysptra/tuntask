package com.rendysaptra.task.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({JwtProperties.class, DataSourceProperties.class})
public class ValidationConfig {
}
