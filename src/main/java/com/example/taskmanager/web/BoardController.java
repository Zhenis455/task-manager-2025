package com.example.taskmanager.web;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;
import com.example.taskmanager.service.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
public class BoardController {
    private final TaskService service;
    public BoardController(TaskService service) { this.service = service; }

    @GetMapping("/board")
    public String board(Model model) {
        List<Task> tasks = service.findAll("", null, null);
        model.addAttribute("todoTasks", tasks.stream().filter(t -> t.getStatus() == TaskStatus.TODO).toList());
        model.addAttribute("inProgressTasks", tasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).toList());
        model.addAttribute("doneTasks", tasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).toList());
        model.addAttribute("stats", service.summary());
        return "tasks/board";
    }

    @PostMapping("/board/{id}/move")
    public String move(@PathVariable long id, @RequestParam TaskStatus status, RedirectAttributes messages) {
        service.changeStatus(id, status);
        messages.addFlashAttribute("success", "Task moved to " + status.getLabel() + ".");
        return "redirect:/board";
    }
}
