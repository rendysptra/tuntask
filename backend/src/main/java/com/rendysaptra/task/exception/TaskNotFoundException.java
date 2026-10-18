package com.rendysaptra.task.exception;

import java.util.UUID;

public class TaskNotFoundException extends RuntimeException {
    
    private final UUID id;
    
    public TaskNotFoundException(UUID id) {
        super(String.format("Task not found with ID: %s", id));
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

}
