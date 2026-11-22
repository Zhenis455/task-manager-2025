package com.example.taskmanager.service;

import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.TaskStatus;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.web.TaskForm;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@ConditionalOnProperty(name = "app.demo-data", havingValue = "true")
public class DemoData implements CommandLineRunner {
    private final TaskRepository repository;
    private final TaskService service;
    public DemoData(TaskRepository repository, TaskService service) {
        this.repository = repository;
        this.service = service;
    }

    @Override
    public void run(String... args) throws Exception {
        // Keep demo setup idempotent even when users delete every task later.
        Path marker = Path.of(".data", ".sample-data-created");
        if (Files.exists(marker)) return;
        Files.createDirectories(marker.getParent());
        if (repository.count() > 0) {
            Files.writeString(marker, "Existing database - no demo data inserted.\n");
            return;
        }
        add("Finish Spring Boot project", "Polish the task manager UI and verify all features.",
                "Development", Priority.HIGH, TaskStatus.IN_PROGRESS, 2);
        add("Review Java OOP", "Practice interfaces, collections and streams.",
                "Study", Priority.MEDIUM, TaskStatus.TODO, 4);
        add("Upload project to GitHub", "Add README, tests and first release.",
                "Portfolio", Priority.HIGH, TaskStatus.TODO, 1);
        add("Plan next sprint", "Choose features for version 1.1.",
                "Work", Priority.LOW, TaskStatus.DONE, -1);
        add("Read Spring Boot docs", "Controllers, services and validation.",
                "Study", Priority.LOW, TaskStatus.TODO, 7);
        Files.writeString(marker, "Sample tasks were created on the first launch.\n");
    }

    private void add(String title, String description, String category,
                     Priority priority, TaskStatus status, int dueInDays) {
        TaskForm form = new TaskForm();
        form.setTitle(title);
        form.setDescription(description);
        form.setCategory(category);
        form.setPriority(priority);
        form.setStatus(status);
        form.setDueDate(LocalDate.now().plusDays(dueInDays));
        service.create(form);
    }
}
