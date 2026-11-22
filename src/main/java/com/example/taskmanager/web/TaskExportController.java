package com.example.taskmanager.web;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.service.TaskService;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TaskExportController {
    private final TaskService service;
    public TaskExportController(TaskService service) { this.service = service; }

    @GetMapping("/tasks/export.csv")
    public ResponseEntity<byte[]> export() {
        List<Task> tasks = service.findAll("", null, null);
        StringBuilder csv = new StringBuilder("\uFEFF");
        csv.append("ID,Title,Description,Category,Status,Priority,Due Date,Created,Completed\r\n");
        for (Task t : tasks) {
            csv.append(t.getId()).append(',')
                    .append(cell(t.getTitle())).append(',')
                    .append(cell(t.getDescription())).append(',')
                    .append(cell(t.getCategory())).append(',')
                    .append(cell(t.getStatus().name())).append(',')
                    .append(cell(t.getPriority().name())).append(',')
                    .append(cell(t.getDueDate() == null ? "" : t.getDueDate().toString())).append(',')
                    .append(cell(t.getCreatedAt() == null ? "" : t.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))).append(',')
                    .append(cell(t.getCompletedAt() == null ? "" : t.getCompletedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))).append("\r\n");
        }
        byte[] body = csv.toString().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=task-manager-export.csv")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .contentLength(body.length).body(body);
    }

    static String cell(String value) {
        String safe = value == null ? "" : value.replace("\r", " ").replace("\n", " ");
        if (!safe.isEmpty() && "=+-@".indexOf(safe.charAt(0)) >= 0) safe = "'" + safe;
        return '"' + safe.replace("\"", "\"\"") + '"';
    }
}
