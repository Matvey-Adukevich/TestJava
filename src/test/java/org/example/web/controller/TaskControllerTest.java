package org.example.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.datasource.model.TaskEntity;
import org.example.datasource.repository.TaskRepository;
import org.example.domain.model.TaskStatus;
import org.example.web.model.CreateTaskRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    public void createTask_ValidRequest_ReturnsTaskId() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest("test text", "EN");

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    public void createTask_TextTooShort_ReturnsBadRequest() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest("ab", "EN");

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createTask_InvalidLanguage_ReturnsBadRequest() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest("test text", "DE");

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createTask_TextOnlyDigits_ReturnsBadRequest() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest("123", "EN");

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getTask_TaskExists_ReturnsTask() throws Exception {
        TaskEntity entity = new TaskEntity();
        entity.setText("test text");
        entity.setStatus(TaskStatus.NEW);
        entity.setLanguage("EN");
        TaskEntity savedEntity = taskRepository.save(entity);

        mockMvc.perform(get("/tasks/" + savedEntity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedEntity.getId().toString()))
                .andExpect(jsonPath("$.status").value("NEW"));
    }

    @Test
    public void getTask_TaskNotFound_ReturnsNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/tasks/" + nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(40401));
    }
}
