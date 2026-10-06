package org.example.scheduler;

import org.example.domain.model.Task;
import org.example.domain.model.TaskStatus;
import org.example.domain.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskProcessingScheduler {
    private static final Logger logger = LoggerFactory.getLogger(TaskProcessingScheduler.class);

    private final TaskService taskService;

    public TaskProcessingScheduler(TaskService taskService) {
        this.taskService = taskService;
    }

    @Scheduled(fixedRate = 5000)
    public void processTasks() {
        List<Task> tasks = taskService.findTasksByStatus(TaskStatus.NEW);

        for (Task task : tasks) {
            try {
                task.setStatus(TaskStatus.IN_PROGRESS);
                taskService.updateTask(task);

                logger.info("Processing task with id: {}", task.getId());

                String correctedText = taskService.correctText(task.getText(), task.getLanguage());

                task.setCorrectedText(correctedText);
                task.setStatus(TaskStatus.COMPLETED);
                taskService.updateTask(task);

                logger.info("Task with id: {} completed successfully", task.getId());
            } catch (Exception e) {
                logger.error("Error processing task with id: {}", task.getId(), e);
                task.setStatus(TaskStatus.ERROR);
                task.setErrorMessage(e.getMessage());
                taskService.updateTask(task);
            }
        }
    }
}
