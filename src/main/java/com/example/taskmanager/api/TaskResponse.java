package com.example.taskmanager.api;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;
import com.example.taskmanager.model.Priority;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskResponse(
        Long id, String title, String description, String category,
        TaskStatus status, Priority priority, LocalDate dueDate,
        boolean overdue, LocalDateTime createdAt, LocalDateTime updatedAt,
        LocalDateTime completedAt) {
    public static TaskResponse from(Task task) {
        return new TaskResponse(task.getId(), task.getTitle(), task.getDescription(),
                task.getCategory(), task.getStatus(), task.getPriority(), task.getDueDate(),
                task.isOverdue(), task.getCreatedAt(), task.getUpdatedAt(), task.getCompletedAt());
    }
}
