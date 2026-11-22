package com.example.taskmanager.service;

import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.web.TaskForm;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class TaskService {
    private final TaskRepository repository;

    public TaskService(TaskRepository repository) { this.repository = repository; }

    @Transactional(readOnly = true)
    public List<Task> findAll(String q, TaskStatus status, Priority priority) {
        final String search = q == null ? "" : q.trim().toLowerCase(Locale.ROOT);
        return repository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .filter(t -> status == null || t.getStatus() == status)
                .filter(t -> priority == null || t.getPriority() == priority)
                .filter(t -> search.isBlank()
                        || t.getTitle().toLowerCase(Locale.ROOT).contains(search)
                        || (t.getDescription() != null && t.getDescription().toLowerCase(Locale.ROOT).contains(search))
                        || t.getCategory().toLowerCase(Locale.ROOT).contains(search))
                .toList();
    }

    @Transactional(readOnly = true)
    public Task get(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
    }

    @Transactional(readOnly = true)
    public Summary summary() {
        List<Task> tasks = repository.findAll();
        long todo = tasks.stream().filter(t -> t.getStatus() == TaskStatus.TODO).count();
        long progress = tasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count();
        long done = tasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count();
        long overdue = tasks.stream().filter(Task::isOverdue).count();
        int percentage = tasks.isEmpty() ? 0 : (int) Math.round(done * 100.0 / tasks.size());
        return new Summary(tasks.size(), todo, progress, done, overdue, percentage);
    }

    public record Summary(long total, long todo, long progress, long done,
                          long overdue, int percentage) { }

    @Transactional
    public Task create(TaskForm form) {
        Task task = new Task();
        apply(task, form);
        return repository.save(task);
    }

    @Transactional
    public Task update(long id, TaskForm form) {
        Task task = get(id);
        apply(task, form);
        return repository.save(task);
    }

    @Transactional
    public void complete(long id) {
        Task task = get(id);
        task.setStatus(task.getStatus() == TaskStatus.DONE ? TaskStatus.TODO : TaskStatus.DONE);
        task.setCompletedAt(task.getStatus() == TaskStatus.DONE ? LocalDateTime.now() : null);
        repository.save(task);
    }

    @Transactional
    public Task changeStatus(long id, TaskStatus status) {
        if (status == null) throw new IllegalArgumentException("Status is required");
        Task task = get(id);
        TaskStatus previous = task.getStatus();
        task.setStatus(status);
        if (status == TaskStatus.DONE && (previous != TaskStatus.DONE || task.getCompletedAt() == null)) {
            task.setCompletedAt(LocalDateTime.now());
        } else if (status != TaskStatus.DONE) {
            task.setCompletedAt(null);
        }
        return repository.save(task);
    }

    @Transactional
    public void delete(long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
        }
        repository.deleteById(id);
    }

    private void apply(Task task, TaskForm form) {
        task.setTitle(form.getTitle().trim());
        task.setDescription(form.getDescription() == null ? "" : form.getDescription().trim());
        task.setCategory(form.getCategory().trim());
        task.setPriority(form.getPriority());
        task.setDueDate(form.getDueDate());
        TaskStatus previous = task.getStatus();
        task.setStatus(form.getStatus());
        if (task.getStatus() == TaskStatus.DONE && (previous != TaskStatus.DONE || task.getCompletedAt() == null)) {
            task.setCompletedAt(LocalDateTime.now());
        } else if (task.getStatus() != TaskStatus.DONE) {
            task.setCompletedAt(null);
        }
    }

    public TaskForm toForm(Task task) {
        TaskForm form = new TaskForm();
        form.setTitle(task.getTitle());
        form.setDescription(task.getDescription());
        form.setCategory(task.getCategory());
        form.setStatus(task.getStatus());
        form.setPriority(task.getPriority());
        form.setDueDate(task.getDueDate());
        return form;
    }
}
