package com.example.taskmanager.api;

import com.example.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskApiIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired TaskRepository repository;
    @BeforeEach void reset() { repository.deleteAll(); }

    @Test void createsListsUpdatesAndDeletesTasksThroughJson() throws Exception {
        String payload = """
                {"title":"Team API","description":"Integrate the client","due_date":"2026-12-20"}
                """;
        mvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.title").value("Team API"))
                .andExpect(jsonPath("$.dueDate").value("2026-12-20"));
        long id = repository.findAll().getFirst().getId();
        mvc.perform(get("/api/tasks")).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value((int) id));
        mvc.perform(get("/api/tasks/stats")).andExpect(status().isOk()).andExpect(jsonPath("$.total").value(1));
        mvc.perform(patch("/api/tasks/" + id + "/status")
                .contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"DONE\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("DONE"));
        mvc.perform(put("/api/tasks/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Updated title\",\"status\":\"IN_PROGRESS\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("IN_PROGRESS"));
        mvc.perform(delete("/api/tasks/" + id)).andExpect(status().isNoContent());
        assertEquals(0, repository.count());
    }

    @Test void preventsEmptyTitles() throws Exception {
        mvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"   \"}"))
                .andExpect(status().isBadRequest());
        assertEquals(0, repository.count());
    }

    @Test void kanbanAndCsvAreAccessible() throws Exception {
        mvc.perform(get("/board")).andExpect(status().isOk()).andExpect(view().name("tasks/board"));
        mvc.perform(get("/tasks/export.csv")).andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=task-manager-export.csv"));
    }
}
