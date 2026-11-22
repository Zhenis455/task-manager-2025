package com.example.taskmanager.service;

import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.web.TaskForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock TaskRepository repository;
    @InjectMocks TaskService service;
    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1L);
        task.setTitle("Study Spring Boot");
        task.setDescription("Read the documentation");
        task.setCategory("Study");
        task.setStatus(TaskStatus.TODO);
        task.setPriority(Priority.HIGH);
        task.setDueDate(LocalDate.now().minusDays(1));
    }

    @Test void canFilterTasksBySearchAndStatus() {
        when(repository.findAll(any(Sort.class))).thenReturn(List.of(task));
        assertEquals(1, service.findAll("spring", TaskStatus.TODO, Priority.HIGH).size());
        assertTrue(service.findAll("other", TaskStatus.TODO, Priority.HIGH).isEmpty());
        assertTrue(service.findAll("spring", TaskStatus.DONE, null).isEmpty());
    }

    @Test void canCreateTask() {
        TaskForm form = new TaskForm();
        form.setTitle("  New task  ");
        when(repository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Task saved = service.create(form);
        assertEquals("New task", saved.getTitle());
        assertEquals(TaskStatus.TODO, saved.getStatus());
    }

    @Test void canCompleteAndReopenTask() {
        when(repository.findById(1L)).thenReturn(Optional.of(task));
        service.complete(1L);
        assertEquals(TaskStatus.DONE, task.getStatus());
        assertNotNull(task.getCompletedAt());
        service.complete(1L);
        assertEquals(TaskStatus.TODO, task.getStatus());
        assertNull(task.getCompletedAt());
        verify(repository, times(2)).save(task);
    }

    @Test void calculatesDashboardStatistics() {
        Task completed = new Task();
        completed.setTitle("Finished");
        completed.setStatus(TaskStatus.DONE);
        when(repository.findAll()).thenReturn(List.of(task, completed));
        TaskService.Summary summary = service.summary();
        assertEquals(2, summary.total());
        assertEquals(1, summary.todo());
        assertEquals(1, summary.done());
        assertEquals(1, summary.overdue());
        assertEquals(50, summary.percentage());
    }

    @Test void throws404ForMissingTask() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> service.get(99L));
    }
    @Test void transitionsKeepCompletionTimestampConsistent() {
        when(repository.findById(1L)).thenReturn(Optional.of(task));
        when(repository.save(task)).thenReturn(task);
        service.changeStatus(1L, TaskStatus.DONE);
        assertNotNull(task.getCompletedAt());
        service.changeStatus(1L, TaskStatus.IN_PROGRESS);
        assertNull(task.getCompletedAt());
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
    }

}
