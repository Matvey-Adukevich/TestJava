package org.example.web.mapper;

import org.example.domain.model.Task;
import org.example.web.model.TaskResponse;

public class TaskMapper {
    public static TaskResponse toResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setStatus(task.getStatus());
        if (task.getStatus() == org.example.domain.model.TaskStatus.COMPLETED) {
            response.setCorrectedText(task.getCorrectedText());
        } else if (task.getStatus() == org.example.domain.model.TaskStatus.ERROR) {
            response.setErrorMessage(task.getErrorMessage());
        }
        return response;
    }
}
