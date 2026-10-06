package org.example.web.controller;

import jakarta.validation.Valid;
import org.example.domain.model.Task;
import org.example.domain.service.TaskService;
import org.example.exception.TaskNotFoundException;
import org.example.exception.ValidationException;
import org.example.web.mapper.TaskMapper;
import org.example.web.model.CreateTaskRequest;
import org.example.web.model.TaskResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<UUID> createTask(@Valid @RequestBody CreateTaskRequest request) {
        logger.info("Received request to create task with language: {}", request.getLanguage());

        validateText(request.getText());

        UUID taskId = taskService.createTask(request.getText(), request.getLanguage());

        logger.info("Task created with id: {}", taskId);

        return ResponseEntity.ok(taskId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable UUID id) {
        logger.info("Received request to get task with id: {}", id);

        Task task = taskService.getTaskById(id);

        return ResponseEntity.ok(TaskMapper.toResponse(task));
    }

    private void validateText(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new ValidationException("Text cannot be empty");
        }

        String trimmedText = text.trim();
        if (trimmedText.length() < 3) {
            throw new ValidationException("Text must contain at least 3 characters");
        }

        boolean hasLetters = trimmedText.chars().anyMatch(Character::isLetter);
        if (!hasLetters) {
            throw new ValidationException("Text cannot contain only special characters and digits");
        }
    }
}
