package com.saramageste.taskboard.task.dto;

import com.saramageste.taskboard.task.enums.Priority;
import com.saramageste.taskboard.task.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TaskRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(max = 30, message = "Title must be at most 30 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 100, message = "Description must be at most 100 characters")
    private String description;

    @NotBlank(message = "Assignee is required")
    @Size(max = 25, message = "Assignee must be at most 25 characters")
    private String assignee;

    @NotNull(message = "Priority is required")
    private Priority priority;

    @NotNull(message = "Status is required")
    private Status status;
}
