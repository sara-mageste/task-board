package com.saramageste.taskboard.task.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saramageste.taskboard.task.dto.TaskRequestDTO;
import com.saramageste.taskboard.task.dto.TaskResponseDTO;
import com.saramageste.taskboard.task.entity.Task;
import com.saramageste.taskboard.task.enums.Priority;
import com.saramageste.taskboard.task.enums.Status;
import com.saramageste.taskboard.task.exception.ResourceNotFoundException;
import com.saramageste.taskboard.task.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    @Test
    void shouldCreateTaskAndReturn200() throws Exception {
        TaskRequestDTO dto = new TaskRequestDTO();
        dto.setTitle("New task");
        dto.setDescription("Description");
        dto.setAssignee("John");
        dto.setPriority(Priority.LOW);
        dto.setStatus(Status.BACKLOG);

        TaskResponseDTO response = new TaskResponseDTO();
        response.setId(1L);
        response.setTitle("New task");
        response.setDescription("Description");
        response.setAssignee("John");
        response.setPriority(Priority.LOW);
        response.setStatus(Status.BACKLOG);

        when(taskService.create(any(TaskRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New task"));
    }

    @Test
    void shouldReturn400WhenCreatingTaskWithBlankTitle() throws Exception {
        TaskRequestDTO dto = new TaskRequestDTO();
        dto.setTitle(""); // invalid - @NotBlank
        dto.setDescription("Description");
        dto.setAssignee("John");
        dto.setPriority(Priority.LOW);
        dto.setStatus(Status.BACKLOG);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").exists());
    }

    @Test
    void shouldReturnTaskWhenFoundById() throws Exception {
        Task task = Task.builder()
                .id(1L)
                .title("Fix login bug")
                .description("Description")
                .assignee("Maria")
                .priority(Priority.HIGH)
                .status(Status.BACKLOG)
                .build();

        when(taskService.findById(1L)).thenReturn(task);

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Fix login bug"));
    }

    @Test
    void shouldReturn404WhenTaskNotFound() throws Exception {
        when(taskService.findById(99L))
                .thenThrow(new ResourceNotFoundException("Task not found with id: 99"));

        mockMvc.perform(get("/tasks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Task not found with id: 99"));
    }

    @Test
    void shouldDeleteTaskAndReturn204() throws Exception {
        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldUpdateStatusAndReturn200() throws Exception {
        Task task = Task.builder()
                .id(1L)
                .title("Fix login bug")
                .status(Status.IN_PROGRESS)
                .build();

        when(taskService.updateStatus(eq(1L), eq(Status.IN_PROGRESS))).thenReturn(task);

        mockMvc.perform(patch("/tasks/1/status").param("status", "IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }
}