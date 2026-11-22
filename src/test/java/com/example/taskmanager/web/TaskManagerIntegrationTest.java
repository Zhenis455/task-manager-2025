package com.example.taskmanager.web;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskManagerIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired TaskRepository repository;

    @BeforeEach void clear() { repository.deleteAll(); }

    @Test void homepageAndEmptyListRender() throws Exception {
        mvc.perform(get("/")).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));
        mvc.perform(get("/tasks")).andExpect(status().isOk())
                .andExpect(view().name("tasks/list"));
    }

    @Test void createEditCompleteDeleteWorkflow() throws Exception {
        mvc.perform(post("/tasks")
                .param("title", "Write README")
                .param("description", "Explain how to run")
                .param("category", "Portfolio")
                .param("status", "TODO")
                .param("priority", "HIGH")
                .param("dueDate", "2026-12-20"))
                .andExpect(status().is3xxRedirection());
        assertEquals(1, repository.count());
        Task task = repository.findAll().getFirst();
        mvc.perform(get("/tasks/" + task.getId() + "/edit")).andExpect(status().isOk());
        mvc.perform(post("/tasks/" + task.getId())
                .param("title", "Updated README")
                .param("category", "Portfolio")
                .param("status", "IN_PROGRESS")
                .param("priority", "MEDIUM"))
                .andExpect(status().is3xxRedirection());
        assertEquals("Updated README", repository.findById(task.getId()).orElseThrow().getTitle());
        mvc.perform(post("/tasks/" + task.getId() + "/complete")).andExpect(status().is3xxRedirection());
        assertEquals("DONE", repository.findById(task.getId()).orElseThrow().getStatus().name());
        mvc.perform(post("/tasks/" + task.getId() + "/delete")).andExpect(status().is3xxRedirection());
        assertEquals(0, repository.count());
    }

    @Test void rejectsInvalidTaskTitle() throws Exception {
        mvc.perform(post("/tasks").param("title", " ").param("category", "Work")
                .param("status", "TODO").param("priority", "MEDIUM"))
                .andExpect(status().isOk()).andExpect(view().name("tasks/form"));
        assertEquals(0, repository.count());
    }
}
