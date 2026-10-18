package com.rendysaptra.task.domain;

import java.time.LocalDate;
import com.rendysaptra.task.domain.entitty.TaskPriority;

public record CreateTaskRequest(
    String title,
    String description,
    LocalDate dueDate,
    TaskPriority priority
) {

}
