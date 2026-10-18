package com.rendysaptra.task.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import com.rendysaptra.task.domain.CreateTaskRequest;
import com.rendysaptra.task.domain.UpdateTaskRequest;
import com.rendysaptra.task.domain.entitty.Task;
import com.rendysaptra.task.domain.entitty.TaskPriority;
import com.rendysaptra.task.domain.entitty.TaskStatus;
import com.rendysaptra.task.exception.TaskNotFoundException;
import com.rendysaptra.task.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskServiceImpl(taskRepository);
    }

    // ===== createTask Tests =====
    @Test
    void createTask_Success() {
        // Arrange
        CreateTaskRequest request = new CreateTaskRequest("Test Task", "Test Description", 
            LocalDate.of(2026, 12, 31), TaskPriority.HIGH);
        Task savedTask = new Task(UUID.randomUUID(), "Test Task", "Test Description",
            LocalDate.of(2026, 12, 31), TaskStatus.TODO, TaskPriority.HIGH,
            Instant.now(), Instant.now());
        
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // Act
        Task result = taskService.createTask(request);

        // Assert
        assertNotNull(result);
        assertEquals("Test Task", result.getTitle());
        assertEquals("Test Description", result.getDescription());
        assertEquals(TaskStatus.TODO, result.getStatus());
        assertEquals(TaskPriority.HIGH, result.getPriority());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void createTask_SetsDefaultStatus() {
        // Arrange
        CreateTaskRequest request = new CreateTaskRequest("New Task", "Description",
            LocalDate.of(2026, 12, 31), TaskPriority.MEDIUM);
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Task result = taskService.createTask(request);

        // Assert
        assertEquals(TaskStatus.TODO, result.getStatus());
    }

    @Test
    void createTask_SetsTimestamps() {
        // Arrange
        CreateTaskRequest request = new CreateTaskRequest("Task", "Desc",
            LocalDate.of(2026, 12, 31), TaskPriority.LOW);
        
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Instant beforeCreate = Instant.now();
        Task result = taskService.createTask(request);
        Instant afterCreate = Instant.now();

        // Assert
        assertNotNull(result.getCreated());
        assertNotNull(result.getUpdated());
        assertTrue(result.getCreated().isAfter(beforeCreate) || result.getCreated().equals(beforeCreate));
        assertTrue(result.getUpdated().isAfter(beforeCreate) || result.getUpdated().equals(beforeCreate));
        assertTrue(result.getCreated().isBefore(afterCreate) || result.getCreated().equals(afterCreate));
    }

    // ===== listTasks Tests =====
    @Test
    void listTasks_ReturnsSortedByCreated() {
        // Arrange
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        Task task1 = new Task(id1, "Task 1", "Desc 1", LocalDate.of(2026, 12, 31),
            TaskStatus.TODO, TaskPriority.HIGH, Instant.now().minusSeconds(10), Instant.now().minusSeconds(10));
        Task task2 = new Task(id2, "Task 2", "Desc 2", LocalDate.of(2026, 12, 31),
            TaskStatus.TODO, TaskPriority.MEDIUM, Instant.now(), Instant.now());
        
        when(taskRepository.findAll(any(Sort.class))).thenReturn(List.of(task1, task2));

        // Act
        List<Task> result = taskService.listTasks();

        // Assert
        assertEquals(2, result.size());
        assertEquals(id1, result.get(0).getId());
        assertEquals(id2, result.get(1).getId());
        verify(taskRepository).findAll(any(Sort.class));
    }

    @Test
    void listTasks_Empty() {
        // Arrange
        when(taskRepository.findAll(any(Sort.class))).thenReturn(Collections.emptyList());

        // Act
        List<Task> result = taskService.listTasks();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(taskRepository).findAll(any(Sort.class));
    }

    // ===== updateTask Tests =====
    @Test
    void updateTask_Success() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        Task existingTask = new Task(taskId, "Old Title", "Old Description",
            LocalDate.of(2026, 12, 31), TaskStatus.TODO, TaskPriority.HIGH,
            Instant.now().minusSeconds(100), Instant.now().minusSeconds(100));
        UpdateTaskRequest updateRequest = new UpdateTaskRequest("New Title", "New Description",
            LocalDate.of(2026, 12, 25), TaskStatus.IN_PROGRESS, TaskPriority.LOW);
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Task result = taskService.updateTask(taskId, updateRequest);

        // Assert
        assertEquals("New Title", result.getTitle());
        assertEquals("New Description", result.getDescription());
        assertEquals(LocalDate.of(2026, 12, 25), result.getDueDate());
        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
        assertEquals(TaskPriority.LOW, result.getPriority());
        verify(taskRepository).findById(taskId);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void updateTask_UpdatesTimestamp() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        Instant oldUpdate = Instant.now().minusSeconds(100);
        Task existingTask = new Task(taskId, "Title", "Desc",
            LocalDate.of(2026, 12, 31), TaskStatus.TODO, TaskPriority.HIGH,
            Instant.now().minusSeconds(200), oldUpdate);
        UpdateTaskRequest updateRequest = new UpdateTaskRequest("Title", "Desc",
            LocalDate.of(2026, 12, 31), TaskStatus.TODO, TaskPriority.HIGH);
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Instant beforeUpdate = Instant.now();
        Task result = taskService.updateTask(taskId, updateRequest);
        Instant afterUpdate = Instant.now();

        // Assert
        assertNotNull(result.getUpdated());
        assertTrue(result.getUpdated().isAfter(oldUpdate));
        assertTrue(result.getUpdated().isAfter(beforeUpdate) || result.getUpdated().equals(beforeUpdate));
        assertTrue(result.getUpdated().isBefore(afterUpdate) || result.getUpdated().equals(afterUpdate));
    }

    @Test
    void updateTask_TaskNotFound() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        UpdateTaskRequest updateRequest = new UpdateTaskRequest("Title", "Desc",
            LocalDate.of(2026, 12, 31), TaskStatus.TODO, TaskPriority.HIGH);
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(taskId, updateRequest));
        verify(taskRepository).findById(taskId);
        verify(taskRepository, never()).save(any(Task.class));
    }

    // ===== deleteTask Tests =====
    @Test
    void deleteTask_Success() {
        // Arrange
        UUID taskId = UUID.randomUUID();

        // Act
        taskService.deleteTask(taskId);

        // Assert
        verify(taskRepository).deleteById(taskId);
    }
}
