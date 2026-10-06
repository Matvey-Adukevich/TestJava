package org.example.domain.service;

import org.example.datasource.mapper.TaskMapper;
import org.example.datasource.model.TaskEntity;
import org.example.datasource.repository.TaskRepository;
import org.example.domain.model.Task;
import org.example.domain.model.TaskStatus;
import org.example.exception.TaskNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final TextCorrectionService textCorrectionService;

    public TaskService(TaskRepository taskRepository, TextCorrectionService textCorrectionService) {
        this.taskRepository = taskRepository;
        this.textCorrectionService = textCorrectionService;
    }

    public UUID createTask(String text, String language) {
        Task task = new Task(text, TaskStatus.NEW, language);
        TaskEntity entity = TaskMapper.toEntity(task);
        TaskEntity savedEntity = taskRepository.save(entity);
        return savedEntity.getId();
    }

    public Task getTaskById(UUID id) {
        TaskEntity entity = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with id: " + id + " not found"));
        return TaskMapper.toDomain(entity);
    }

    public List<Task> findTasksByStatus(TaskStatus status) {
        List<TaskEntity> entities = taskRepository.findByStatus(status);
        return entities.stream()
                .map(TaskMapper::toDomain)
                .collect(Collectors.toList());
    }

    public void updateTask(Task task) {
        TaskEntity entity = TaskMapper.toEntity(task);
        taskRepository.save(entity);
    }

    public String correctText(String text, String language) {
        return textCorrectionService.correctText(text, language);
    }
}
