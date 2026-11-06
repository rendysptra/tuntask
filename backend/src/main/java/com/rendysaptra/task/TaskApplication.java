package com.rendysaptra.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class TaskApplication {

	public static void main(String[] args) {
	Dotenv dotenv = Dotenv.configure()
				.directory("../")
				.ignoreIfMissing()
				.load();
		
		dotenv.entries().forEach(entry -> 
			System.setProperty(entry.getKey(), entry.getValue())
		);
		
		// Fail-fast validation before Spring context initializes
		validateRequiredEnvVars();
		
		SpringApplication.run(TaskApplication.class, args);
	}
	
	private static void validateRequiredEnvVars() {
		validateNotBlank(System.getProperty("JWT_SECRET"), "JWT_SECRET");
		validateNotBlank(System.getProperty("DB_USER"), "DB_USER");
		validateNotBlank(System.getProperty("DB_PASSWORD"), "DB_PASSWORD");
		validateNotBlank(System.getProperty("SPRING_DATASOURCE_URL", System.getProperty("DB_NAME")), 
			"SPRING_DATASOURCE_URL or DB_NAME");
		
		String jwtSecret = System.getProperty("JWT_SECRET");
		if (jwtSecret != null && jwtSecret.trim().length() < 64) {
			throw new IllegalStateException("FATAL: JWT_SECRET must be at least 64 characters, got " + jwtSecret.trim().length());
		}
	}
	
	private static void validateNotBlank(String value, String name) {
		if (value == null || value.trim().isEmpty()) {
			throw new IllegalStateException("FATAL: " + name + " must be set and not empty");
		}
	}

}
