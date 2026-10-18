package com.rendysaptra.task.domain.dto;

import java.time.LocalDate;

import org.hibernate.validator.constraints.Length;

import com.rendysaptra.task.domain.entitty.TaskPriority;
import com.rendysaptra.task.domain.entitty.TaskStatus;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateTaskRequestDto(
    
    @NotBlank(message = ERROR_MESSAGE_TITLE_LENGTH)
    @Length(max = 255, message = ERROR_MESSAGE_TITLE_LENGTH)
    String title,

    @Length(max = 1000, message= ERROR_MESSAGE_DESCRIPTION_LENGTH)
    @Nullable
    String description,

    @FutureOrPresent(message = ERROR_MESSAGE_DUE_DATE)
    @Nullable
    LocalDate dueDate,

    @NotNull(message = ERROR_MESSAGE_PRIORITY)
    TaskPriority priority,

    @NotNull(message = ERROR_MESSAGE_STATUS)
    TaskStatus status
) {

    private static final String ERROR_MESSAGE_TITLE_LENGTH =
        "Title must between 1 and 255 characters";

    private static final String ERROR_MESSAGE_DESCRIPTION_LENGTH =
        "Description must be less than 1000 characters";
    
    private static final String ERROR_MESSAGE_DUE_DATE =
        "Due date must be in the present or future";

    private static final String ERROR_MESSAGE_PRIORITY =
        "Task Priority must be provided";

    private static final String ERROR_MESSAGE_STATUS =
        "Task Status must be provided";
}
