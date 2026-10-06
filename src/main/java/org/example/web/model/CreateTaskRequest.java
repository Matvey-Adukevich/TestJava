package org.example.web.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateTaskRequest {
    @NotBlank(message = "Text is required")
    @Size(min = 3, message = "Text must contain at least 3 characters")
    private String text;

    @NotBlank(message = "Language is required")
    @Pattern(regexp = "EN|RU", message = "Language must be EN or RU")
    private String language;

    public CreateTaskRequest() {
    }

    public CreateTaskRequest(String text, String language) {
        this.text = text;
        this.language = language;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
