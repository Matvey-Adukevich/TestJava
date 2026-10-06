package org.example.web.model;

import org.example.domain.model.TaskStatus;

import java.util.UUID;

public class TaskResponse {
    private UUID id;
    private TaskStatus status;
    private String correctedText;
    private String errorMessage;

    public TaskResponse() {
    }

    public TaskResponse(UUID id, TaskStatus status, String correctedText, String errorMessage) {
        this.id = id;
        this.status = status;
        this.correctedText = correctedText;
        this.errorMessage = errorMessage;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getCorrectedText() {
        return correctedText;
    }

    public void setCorrectedText(String correctedText) {
        this.correctedText = correctedText;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
