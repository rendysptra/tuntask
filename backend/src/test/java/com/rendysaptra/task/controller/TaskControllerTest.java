package com.rendysaptra.task.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rendysaptra.task.domain.CreateTaskRequest;
import com.rendysaptra.task.domain.UpdateTaskRequest;
import com.rendysaptra.task.domain.dto.CreateTaskRequestDto;
import com.rendysaptra.task.domain.dto.TaskDto;
import com.rendysaptra.task.domain.dto.UpdateTaskRequestDto;
import com.rendysaptra.task.domain.entitty.Task;
import com.rendysaptra.task.domain.entitty.TaskPriority;
import com.rendysaptra.task.domain.entitty.TaskStatus;
import com.rendysaptra.task.exception.TaskNotFoundException;
import com.rendysaptra.task.mapper.TaskMapper;
import com.rendysaptra.task.service.TaskService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private TaskService taskService;

    @Mock
    private TaskMapper taskMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        
        TaskController taskController = new TaskController(taskService, taskMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(taskController)
            .setControllerAdvice(new com.rendysaptra.task.controller.GlobalExceptionHandler())
            .build();
    }

    @Test
    void createTask_Success() throws Exception {
        CreateTaskRequestDto requestDto = new CreateTaskRequestDto(
            "Test Task",
            "Test Description",
            LocalDate.of(2026, 12, 31),
            TaskPriority.HIGH
        );

        CreateTaskRequest request = new CreateTaskRequest(
            "Test Task",
            "Test Description",
            LocalDate.of(2026, 12, 31),
            TaskPriority.HIGH
        );

        UUID taskId = UUID.randomUUID();
        Task task = new Task(
            taskId,
            "Test Task",
            "Test Description",
            LocalDate.of(2026, 12, 31),
            TaskStatus.TODO,
            TaskPriority.HIGH,
            Instant.now(),
            Instant.now()
        );

        TaskDto taskDto = new TaskDto(
            taskId,
            "Test Task",
            "Test Description",
            LocalDate.of(2026, 12, 31),
            TaskPriority.HIGH,
            TaskStatus.TODO
        );

        when(taskMapper.fromDto(any(CreateTaskRequestDto.class))).thenReturn(request);
        when(taskService.createTask(any(CreateTaskRequest.class))).thenReturn(task);
        when(taskMapper.toDto(any(Task.class))).thenReturn(taskDto);

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(taskId.toString()))
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.status").value("TODO"));

        verify(taskMapper).fromDto(any(CreateTaskRequestDto.class));
        verify(taskService).createTask(any(CreateTaskRequest.class));
        verify(taskMapper).toDto(any(Task.class));
    }

    @Test
    void createTask_ValidationFailure_EmptyTitle() throws Exception {
        CreateTaskRequestDto requestDto = new CreateTaskRequestDto(
            "",
            "Description",
            LocalDate.of(2026, 12, 31),
            TaskPriority.HIGH
        );

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).createTask(any(CreateTaskRequest.class));
    }

    @Test
    void createTask_ValidationFailure_NullPriority() throws Exception {
        String requestJson = """
            {
                "title": "Test Task",
                "description": "Description",
                "dueDate": "2026-12-31",
                "priority": null
            }
            """;

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).createTask(any(CreateTaskRequest.class));
    }

    @Test
    void listTasks_Success() throws Exception {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        Task task1 = new Task(
            id1,
            "Task 1",
            "Description 1",
            LocalDate.of(2026, 12, 31),
            TaskStatus.TODO,
            TaskPriority.HIGH,
            Instant.now(),
            Instant.now()
        );

        Task task2 = new Task(
            id2,
            "Task 2",
            "Description 2",
            LocalDate.of(2026, 12, 25),
            TaskStatus.IN_PROGRESS,
            TaskPriority.MEDIUM,
            Instant.now(),
            Instant.now()
        );

        TaskDto taskDto1 = new TaskDto(id1, "Task 1", "Description 1",
            LocalDate.of(2026, 12, 31), TaskPriority.HIGH, TaskStatus.TODO);
        TaskDto taskDto2 = new TaskDto(id2, "Task 2", "Description 2",
            LocalDate.of(2026, 12, 25), TaskPriority.MEDIUM, TaskStatus.IN_PROGRESS);

        when(taskService.listTasks()).thenReturn(Arrays.asList(task1, task2));
        when(taskMapper.toDto(task1)).thenReturn(taskDto1);
        when(taskMapper.toDto(task2)).thenReturn(taskDto2);

        mockMvc.perform(get("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(id1.toString()))
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[1].id").value(id2.toString()))
                .andExpect(jsonPath("$[1].title").value("Task 2"));

        verify(taskService).listTasks();
        verify(taskMapper, times(2)).toDto(any(Task.class));
    }

    @Test
    void listTasks_EmptyList() throws Exception {
        when(taskService.listTasks()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(taskService).listTasks();
    }

    @Test
    void updateTask_Success() throws Exception {
        UUID taskId = UUID.randomUUID();

        UpdateTaskRequestDto requestDto = new UpdateTaskRequestDto(
            "Updated Task",
            "Updated Description",
            LocalDate.of(2026, 12, 25),
            TaskPriority.LOW,
            TaskStatus.DONE
        );

        UpdateTaskRequest request = new UpdateTaskRequest(
            "Updated Task",
            "Updated Description",
            LocalDate.of(2026, 12, 25),
            TaskStatus.DONE,
            TaskPriority.LOW
        );

        Task updatedTask = new Task(
            taskId,
            "Updated Task",
            "Updated Description",
            LocalDate.of(2026, 12, 25),
            TaskStatus.DONE,
            TaskPriority.LOW,
            Instant.now(),
            Instant.now()
        );

        TaskDto taskDto = new TaskDto(
            taskId,
            "Updated Task",
            "Updated Description",
            LocalDate.of(2026, 12, 25),
            TaskPriority.LOW,
            TaskStatus.DONE
        );

        when(taskMapper.fromDto(any(UpdateTaskRequestDto.class))).thenReturn(request);
        when(taskService.updateTask(any(UUID.class), any(UpdateTaskRequest.class))).thenReturn(updatedTask);
        when(taskMapper.toDto(any(Task.class))).thenReturn(taskDto);

        mockMvc.perform(put("/api/v1/tasks/" + taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId.toString()))
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.status").value("DONE"));

        verify(taskMapper).fromDto(any(UpdateTaskRequestDto.class));
        verify(taskService).updateTask(any(UUID.class), any(UpdateTaskRequest.class));
        verify(taskMapper).toDto(any(Task.class));
    }

    @Test
    void updateTask_TaskNotFound() throws Exception {
        UUID taskId = UUID.randomUUID();

        UpdateTaskRequestDto requestDto = new UpdateTaskRequestDto(
            "Updated Task",
            "Updated Description",
            LocalDate.of(2026, 12, 25),
            TaskPriority.LOW,
            TaskStatus.DONE
        );

        UpdateTaskRequest request = new UpdateTaskRequest(
            "Updated Task",
            "Updated Description",
            LocalDate.of(2026, 12, 25),
            TaskStatus.DONE,
            TaskPriority.LOW
        );

        when(taskMapper.fromDto(any(UpdateTaskRequestDto.class))).thenReturn(request);
        when(taskService.updateTask(any(UUID.class), any(UpdateTaskRequest.class)))
            .thenThrow(new TaskNotFoundException(taskId));

        mockMvc.perform(put("/api/v1/tasks/" + taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());

        verify(taskService).updateTask(any(UUID.class), any(UpdateTaskRequest.class));
    }

    @Test
    void updateTask_ValidationFailure_EmptyTitle() throws Exception {
        UUID taskId = UUID.randomUUID();

        UpdateTaskRequestDto requestDto = new UpdateTaskRequestDto(
            "",
            "Description",
            LocalDate.of(2026, 12, 31),
            TaskPriority.HIGH,
            TaskStatus.TODO
        );

        mockMvc.perform(put("/api/v1/tasks/" + taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).updateTask(any(UUID.class), any(UpdateTaskRequest.class));
    }

    @Test
    void deleteTask_Success() throws Exception {
        UUID taskId = UUID.randomUUID();

        doNothing().when(taskService).deleteTask(any(UUID.class));

        mockMvc.perform(delete("/api/v1/tasks/" + taskId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(taskService).deleteTask(any(UUID.class));
    }

    @Test
    void deleteTask_TaskNotFound() throws Exception {
        UUID taskId = UUID.randomUUID();

        doThrow(new TaskNotFoundException(taskId))
            .when(taskService).deleteTask(any(UUID.class));

        mockMvc.perform(delete("/api/v1/tasks/" + taskId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(taskService).deleteTask(any(UUID.class));
    }
}
