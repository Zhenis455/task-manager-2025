package com.example.taskmanager.web;

import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.TaskStatus;
import com.example.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class TaskController {
    private final TaskService service;

    public TaskController(TaskService service) { this.service = service; }

    @GetMapping("/")
    public String home() { return "redirect:/tasks"; }

    @GetMapping("/tasks")
    public String list(@RequestParam(defaultValue = "") String q,
                       @RequestParam(required = false) TaskStatus status,
                       @RequestParam(required = false) Priority priority, Model model) {
        model.addAttribute("tasks", service.findAll(q, status, priority));
        model.addAttribute("stats", service.summary());
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("q", q);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedPriority", priority);
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("priorities", Priority.values());
        return "tasks/list";
    }

    @GetMapping("/tasks/{id}")
    public String details(@PathVariable long id, Model model) {
        model.addAttribute("task", service.get(id));
        return "tasks/details";
    }

    @GetMapping("/tasks/new")
    public String newTask(Model model) {
        model.addAttribute("form", new TaskForm());
        prepareForm(model, false, null);
        return "tasks/form";
    }

    @PostMapping("/tasks")
    public String create(@Valid @ModelAttribute("form") TaskForm form, BindingResult binding,
                         Model model, RedirectAttributes redirect) {
        if (binding.hasErrors()) {
            prepareForm(model, false, null);
            return "tasks/form";
        }
        service.create(form);
        redirect.addFlashAttribute("success", "Task created successfully.");
        return "redirect:/tasks";
    }

    @GetMapping("/tasks/{id}/edit")
    public String edit(@PathVariable long id, Model model) {
        model.addAttribute("form", service.toForm(service.get(id)));
        prepareForm(model, true, id);
        return "tasks/form";
    }

    @PostMapping("/tasks/{id}")
    public String update(@PathVariable long id, @Valid @ModelAttribute("form") TaskForm form,
                         BindingResult binding, Model model, RedirectAttributes redirect) {
        if (binding.hasErrors()) {
            service.get(id); // 404 if item was deleted while editing
            prepareForm(model, true, id);
            return "tasks/form";
        }
        service.update(id, form);
        redirect.addFlashAttribute("success", "Task updated successfully.");
        return "redirect:/tasks";
    }

    @PostMapping("/tasks/{id}/complete")
    public String complete(@PathVariable long id, RedirectAttributes redirect) {
        service.complete(id);
        redirect.addFlashAttribute("success", "Task status updated.");
        return "redirect:/tasks";
    }

    @PostMapping("/tasks/{id}/delete")
    public String delete(@PathVariable long id, RedirectAttributes redirect) {
        service.delete(id);
        redirect.addFlashAttribute("success", "Task removed.");
        return "redirect:/tasks";
    }

    private void prepareForm(Model model, boolean editing, Long taskId) {
        model.addAttribute("editing", editing);
        model.addAttribute("taskId", taskId);
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("statuses", TaskStatus.values());
    }
}
