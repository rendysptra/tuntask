package com.rendysaptra.task.integration.api;

import com.rendysaptra.task.integration.BaseIntegrationTest;
import com.rendysaptra.task.integration.util.TestDataBuilder;
import com.rendysaptra.task.repository.TaskRepository;
import com.rendysaptra.task.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TaskApiIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    private static final String SIGNUP_URL = "/api/v1/auth/signup";
    private static final String TASKS_URL = "/api/v1/tasks";

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        taskRepository.deleteAll();
        userRepository.deleteAll();
        TestDataBuilder.resetCounter();
    }

    @Test
    void createTask_withAuth_returns201AndCreatedTask() throws Exception {
        String authToken = getAuthToken();
        var request = TestDataBuilder.buildCreateTaskRequest();

        mockMvc.perform(post(TASKS_URL)
                .header("Authorization", getAuthorizationHeader(authToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title", equalTo(request.title())))
            .andExpect(jsonPath("$.description", equalTo(request.description())))
            .andExpect(jsonPath("$.priority", equalTo(request.priority().name())));
    }

    @Test
    void createTask_withoutAuth_returns403Forbidden() throws Exception {
        var request = TestDataBuilder.buildCreateTaskRequest();

        mockMvc.perform(post(TASKS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden());
    }

    @Test
    void createTask_invalidData_returns400BadRequest() throws Exception {
        String authToken = getAuthToken();
        var request = TestDataBuilder.buildCreateTaskRequest("", null, null, null);

        mockMvc.perform(post(TASKS_URL)
                .header("Authorization", getAuthorizationHeader(authToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void listTasks_withAuth_returnsUserTasks() throws Exception {
        String authToken = getAuthToken();
        var createRequest = TestDataBuilder.buildCreateTaskRequest();

        mockMvc.perform(post(TASKS_URL)
                .header("Authorization", getAuthorizationHeader(authToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated());

        mockMvc.perform(get(TASKS_URL)
                .header("Authorization", getAuthorizationHeader(authToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void listTasks_withoutAuth_returns403Forbidden() throws Exception {
        mockMvc.perform(get(TASKS_URL))
            .andExpect(status().isForbidden());
    }

    @Test
    void updateTask_existingTask_returns200AndUpdatedTask() throws Exception {
        String authToken = getAuthToken();
        var createRequest = TestDataBuilder.buildCreateTaskRequest();

        MvcResult createResult = mockMvc.perform(post(TASKS_URL)
                .header("Authorization", getAuthorizationHeader(authToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andReturn();

        String taskId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText();

        var updateRequest = TestDataBuilder.buildUpdateTaskRequest();
        mockMvc.perform(put(TASKS_URL + "/" + taskId)
                .header("Authorization", getAuthorizationHeader(authToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title", equalTo(updateRequest.title())))
            .andExpect(jsonPath("$.status", equalTo(updateRequest.status().name())));
    }

    @Test
    void updateTask_nonExistentTask_returns404NotFound() throws Exception {
        String authToken = getAuthToken();
        var updateRequest = TestDataBuilder.buildUpdateTaskRequest();
        String fakeTaskId = "550e8400-e29b-41d4-a716-446655440000";

        mockMvc.perform(put(TASKS_URL + "/" + fakeTaskId)
                .header("Authorization", getAuthorizationHeader(authToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    void updateTask_withoutAuth_returns403Forbidden() throws Exception {
        var updateRequest = TestDataBuilder.buildUpdateTaskRequest();
        String fakeTaskId = "550e8400-e29b-41d4-a716-446655440000";

        mockMvc.perform(put(TASKS_URL + "/" + fakeTaskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isForbidden());
    }

    @Test
    void deleteTask_existingTask_returns204NoContent() throws Exception {
        String authToken = getAuthToken();
        var createRequest = TestDataBuilder.buildCreateTaskRequest();

        MvcResult createResult = mockMvc.perform(post(TASKS_URL)
                .header("Authorization", getAuthorizationHeader(authToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andReturn();

        String taskId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(delete(TASKS_URL + "/" + taskId)
                .header("Authorization", getAuthorizationHeader(authToken)))
            .andExpect(status().isNoContent());

        mockMvc.perform(get(TASKS_URL)
                .header("Authorization", getAuthorizationHeader(authToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void deleteTask_nonExistentTask_returns204NoContent() throws Exception {
        String authToken = getAuthToken();
        String fakeTaskId = "550e8400-e29b-41d4-a716-446655440000";

        mockMvc.perform(delete(TASKS_URL + "/" + fakeTaskId)
                .header("Authorization", getAuthorizationHeader(authToken)))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteTask_withoutAuth_returns403Forbidden() throws Exception {
        String fakeTaskId = "550e8400-e29b-41d4-a716-446655440000";

        mockMvc.perform(delete(TASKS_URL + "/" + fakeTaskId))
            .andExpect(status().isForbidden());
    }

    private String getAuthToken() throws Exception {
        var signupRequest = TestDataBuilder.buildSignupRequest("taskuser@example.com", "SecurePass123!");

        MvcResult result = mockMvc.perform(post(SIGNUP_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
            .andExpect(status().isCreated())
            .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }
}
