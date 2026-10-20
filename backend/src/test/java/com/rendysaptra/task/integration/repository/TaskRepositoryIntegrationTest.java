package com.rendysaptra.task.integration.repository;

import com.rendysaptra.task.domain.entitty.Task;
import com.rendysaptra.task.domain.entitty.TaskPriority;
import com.rendysaptra.task.domain.entitty.TaskStatus;
import com.rendysaptra.task.integration.BaseIntegrationTest;
import com.rendysaptra.task.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TaskRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        taskRepository.deleteAll();
    }

    @Test
    void save_validTask_persistsToDatabase() {
        Task task = createTask("Test Task", TaskStatus.TODO, TaskPriority.HIGH);

        Task saved = taskRepository.save(task);

        assertNotNull(saved.getId());
        assertEquals("Test Task", saved.getTitle());
        assertNotNull(saved.getCreated());
        assertNotNull(saved.getUpdated());
    }

    @Test
    void findAll_withMultipleTasks_returnsAllTasks() {
        Task task1 = createTask("Task 1", TaskStatus.TODO, TaskPriority.LOW);
        Task task2 = createTask("Task 2", TaskStatus.IN_PROGRESS, TaskPriority.MEDIUM);
        taskRepository.saveAll(List.of(task1, task2));

        List<Task> tasks = taskRepository.findAll();

        assertEquals(2, tasks.size());
    }

    @Test
    void findById_existingTask_returnsTask() {
        Task task = createTask("Test Task", TaskStatus.TODO, TaskPriority.MEDIUM);
        Task saved = taskRepository.save(task);

        Optional<Task> found = taskRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    void findById_nonExistentId_returnsEmpty() {
        Optional<Task> found = taskRepository.findById(UUID.randomUUID());

        assertTrue(found.isEmpty());
    }

    @Test
    void delete_existingTask_removesFromDatabase() {
        Task task = createTask("Test Task", TaskStatus.TODO, TaskPriority.MEDIUM);
        Task saved = taskRepository.save(task);

        taskRepository.delete(saved);

        Optional<Task> found = taskRepository.findById(saved.getId());
        assertTrue(found.isEmpty());
    }

    @Test
    void update_existingTask_updatesFields() {
        Task task = createTask("Original Title", TaskStatus.TODO, TaskPriority.LOW);
        Task saved = taskRepository.save(task);

        saved.setTitle("Updated Title");
        saved.setStatus(TaskStatus.DONE);
        saved.setPriority(TaskPriority.HIGH);
        saved.setUpdated(Instant.now());
        Task updated = taskRepository.save(saved);

        assertEquals("Updated Title", updated.getTitle());
        assertEquals(TaskStatus.DONE, updated.getStatus());
        assertEquals(TaskPriority.HIGH, updated.getPriority());
    }

    @Test
    void save_taskWithDueDate_persistsCorrectly() {
        LocalDate dueDate = LocalDate.now().plusDays(7);
        Task task = createTask("Task with due date", TaskStatus.TODO, TaskPriority.MEDIUM);
        task.setDueDate(dueDate);

        Task saved = taskRepository.save(task);

        assertEquals(dueDate, saved.getDueDate());
    }

    @Test
    void save_taskWithNullDescription_succeeds() {
        Task task = createTask("Task without description", TaskStatus.TODO, TaskPriority.LOW);
        task.setDescription(null);

        Task saved = taskRepository.save(task);

        assertNull(saved.getDescription());
    }

    private Task createTask(String title, TaskStatus status, TaskPriority priority) {
        Task task = new Task();
        task.setTitle(title);
        task.setStatus(status);
        task.setPriority(priority);
        task.setCreated(Instant.now());
        task.setUpdated(Instant.now());
        return task;
    }
}
