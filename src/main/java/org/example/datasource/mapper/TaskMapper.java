package org.example.datasource.mapper;

import org.example.datasource.model.TaskEntity;
import org.example.domain.model.Task;
import org.example.domain.model.TaskStatus;

public class TaskMapper {
    public static TaskEntity toEntity(Task task) {
        TaskEntity entity = new TaskEntity();
        entity.setId(task.getId());
        entity.setText(task.getText());
        entity.setStatus(task.getStatus());
        entity.setLanguage(task.getLanguage());
        entity.setCorrectedText(task.getCorrectedText());
        entity.setErrorMessage(task.getErrorMessage());
        entity.setCreatedAt(task.getCreatedAt());
        entity.setUpdatedAt(task.getUpdatedAt());
        return entity;
    }

    public static Task toDomain(TaskEntity entity) {
        Task task = new Task();
        task.setId(entity.getId());
        task.setText(entity.getText());
        task.setStatus(entity.getStatus());
        task.setLanguage(entity.getLanguage());
        task.setCorrectedText(entity.getCorrectedText());
        task.setErrorMessage(entity.getErrorMessage());
        task.setCreatedAt(entity.getCreatedAt());
        task.setUpdatedAt(entity.getUpdatedAt());
        return task;
    }
}
