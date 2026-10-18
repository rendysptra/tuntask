package com.rendysaptra.task.mapper;

import com.rendysaptra.task.domain.CreateTaskRequest;
import com.rendysaptra.task.domain.UpdateTaskRequest;
import com.rendysaptra.task.domain.dto.CreateTaskRequestDto;
import com.rendysaptra.task.domain.dto.TaskDto;
import com.rendysaptra.task.domain.dto.UpdateTaskRequestDto;
import com.rendysaptra.task.domain.entitty.Task;

public interface TaskMapper {

    CreateTaskRequest fromDto(CreateTaskRequestDto dto);
    
    UpdateTaskRequest fromDto(UpdateTaskRequestDto dto);

    TaskDto toDto(Task task);

}
