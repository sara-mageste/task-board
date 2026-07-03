package com.saramageste.taskboard.task.dto;

import com.saramageste.taskboard.task.enums.Priority;
import com.saramageste.taskboard.task.enums.Status;
import lombok.Data;

@Data
public class TaskRequestDTO {
    private String title;
    private String description;
    private String assignee;
    private Priority priority;
    private Status status;
}
