package com.example.taskmanager.api;

import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.TaskStatus;
import com.example.taskmanager.service.TaskService;
import com.example.taskmanager.web.TaskForm;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/tasks")
@Validated
public class TaskApiController {
    private final TaskService service;
    public TaskApiController(TaskService service) { this.service = service; }

    @GetMapping
    public List<TaskResponse> all(@RequestParam(defaultValue = "") String q,
                                  @RequestParam(required = false) TaskStatus status,
                                  @RequestParam(required = false) Priority priority) {
        return service.findAll(q, status, priority).stream().map(TaskResponse::from).toList();
    }

    @GetMapping("/{id}")
    public TaskResponse one(@PathVariable long id) { return TaskResponse.from(service.get(id)); }

    @GetMapping("/stats")
    public TaskService.Summary stats() { return service.summary(); }

    @PostMapping
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskRequest request) {
        var result = service.create(toForm(request));
        return ResponseEntity.created(URI.create("/api/tasks/" + result.getId()))
                .body(TaskResponse.from(result));
    }

    @PutMapping("/{id}")
    public TaskResponse update(@PathVariable long id, @Valid @RequestBody TaskRequest request) {
        return TaskResponse.from(service.update(id, toForm(request)));
    }

    @PatchMapping("/{id}/status")
    public TaskResponse move(@PathVariable long id, @Valid @RequestBody StatusRequest request) {
        return TaskResponse.from(service.changeStatus(id, request.status()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    public record StatusRequest(@NotNull TaskStatus status) { }

    private TaskForm toForm(TaskRequest req) {
        TaskForm form = new TaskForm();
        form.setTitle(req.title());
        form.setDescription(req.description() == null ? "" : req.description());
        form.setCategory(req.category() == null || req.category().isBlank() ? "Personal" : req.category());
        form.setPriority(req.priority() == null ? Priority.MEDIUM : req.priority());
        form.setStatus(req.status() == null ? TaskStatus.TODO : req.status());
        form.setDueDate(req.dueDate());
        return form;
    }
}
