package com.rendysaptra.task.integration.util;

import com.rendysaptra.task.domain.dto.CreateTaskRequestDto;
import com.rendysaptra.task.domain.dto.LoginRequestDto;
import com.rendysaptra.task.domain.dto.SignupRequestDto;
import com.rendysaptra.task.domain.dto.UpdateTaskRequestDto;
import com.rendysaptra.task.domain.entitty.TaskPriority;
import com.rendysaptra.task.domain.entitty.TaskStatus;

import java.time.LocalDate;
import java.util.UUID;

public class TestDataBuilder {

    private static int counter = 0;

    public static SignupRequestDto buildSignupRequest() {
        return buildSignupRequest("user" + counter++ + "@test.com", "Password123!");
    }

    public static SignupRequestDto buildSignupRequest(String email, String password) {
        SignupRequestDto dto = new SignupRequestDto();
        dto.setEmail(email);
        dto.setPassword(password);
        return dto;
    }

    public static LoginRequestDto buildLoginRequest(String email, String password) {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail(email);
        dto.setPassword(password);
        return dto;
    }

    public static CreateTaskRequestDto buildCreateTaskRequest() {
        return buildCreateTaskRequest(
            "Test Task " + UUID.randomUUID(),
            "Test Description",
            LocalDate.now().plusDays(1),
            TaskPriority.MEDIUM
        );
    }

    public static CreateTaskRequestDto buildCreateTaskRequest(
            String title,
            String description,
            LocalDate dueDate,
            TaskPriority priority) {
        return new CreateTaskRequestDto(title, description, dueDate, priority);
    }

    public static UpdateTaskRequestDto buildUpdateTaskRequest() {
        return buildUpdateTaskRequest(
            "Updated Task " + UUID.randomUUID(),
            "Updated Description",
            LocalDate.now().plusDays(2),
            TaskPriority.HIGH,
            TaskStatus.IN_PROGRESS
        );
    }

    public static UpdateTaskRequestDto buildUpdateTaskRequest(
            String title,
            String description,
            LocalDate dueDate,
            TaskPriority priority,
            TaskStatus status) {
        return new UpdateTaskRequestDto(title, description, dueDate, priority, status);
    }

    public static void resetCounter() {
        counter = 0;
    }
}
