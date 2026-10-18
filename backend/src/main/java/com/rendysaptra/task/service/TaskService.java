package com.rendysaptra.task.service;

import java.util.List;
import java.util.UUID;

import com.rendysaptra.task.domain.CreateTaskRequest;
import com.rendysaptra.task.domain.UpdateTaskRequest;
import com.rendysaptra.task.domain.entitty.Task;

public interface TaskService {

    Task createTask(CreateTaskRequest request);

    List<Task> listTasks();

    Task updateTask(UUID taskId, UpdateTaskRequest request);

    void deleteTask(UUID taskId);

}
