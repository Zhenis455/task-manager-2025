package com.example.taskmanager.web;

import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Getter
@Setter
public class TaskForm {
    @NotBlank(message = "Enter a task title")
    @Size(max = 120, message = "Title must be 120 characters or less")
    private String title = "";

    @Size(max = 1500, message = "Description must be 1500 characters or less")
    private String description = "";

    @NotNull(message = "Choose a status")
    private TaskStatus status = TaskStatus.TODO;

    @NotNull(message = "Choose a priority")
    private Priority priority = Priority.MEDIUM;

    @NotBlank(message = "Enter a category")
    @Size(max = 40, message = "Category must be 40 characters or less")
    private String category = "Personal";

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dueDate;
}
