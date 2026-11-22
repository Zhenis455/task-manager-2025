package com.example.taskmanager.api;

import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.TaskStatus;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record TaskRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 120) String title,
        @Size(max = 1500) String description,
        @Size(max = 40) String category,
        Priority priority,
        TaskStatus status,
        @JsonAlias({"due_date", "deadlineDate"}) LocalDate dueDate
) { }
