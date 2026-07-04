package com.saramageste.taskboard.task.service;

import com.saramageste.taskboard.task.dto.TaskRequestDTO;
import com.saramageste.taskboard.task.dto.TaskResponseDTO;
import com.saramageste.taskboard.task.entity.Task;
import com.saramageste.taskboard.task.enums.Priority;
import com.saramageste.taskboard.task.enums.Status;
import com.saramageste.taskboard.task.exception.ResourceNotFoundException;
import com.saramageste.taskboard.task.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task existingTask;

    @BeforeEach
    void setUp() {
        existingTask = Task.builder()
                .id(1L)
                .title("Fix login bug")
                .description("Users can't log in with Google")
                .assignee("Maria")
                .priority(Priority.HIGH)
                .status(Status.BACKLOG)
                .build();
    }

    @Test
    void shouldCreateTaskSuccessfully() {
        TaskRequestDTO dto = new TaskRequestDTO();
        dto.setTitle("New task");
        dto.setDescription("Some description");
        dto.setAssignee("John");
        dto.setPriority(Priority.MEDIUM);
        dto.setStatus(Status.BACKLOG);

        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        TaskResponseDTO response = taskService.create(dto);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getTitle()).isEqualTo("New task");
        assertThat(response.getAssignee()).isEqualTo("John");
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void shouldReturnTaskWhenFindByIdExists() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));

        Task result = taskService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Fix login bug");
    }

    @Test
    void shouldThrowExceptionWhenFindByIdDoesNotExist() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void shouldUpdateExistingTask() {
        Task updatedData = Task.builder()
                .title("Fix login bug - urgent")
                .description("Updated description")
                .assignee("Maria")
                .priority(Priority.HIGH)
                .status(Status.IN_PROGRESS)
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task result = taskService.update(1L, updatedData);

        assertThat(result.getTitle()).isEqualTo("Fix login bug - urgent");
        assertThat(result.getStatus()).isEqualTo(Status.IN_PROGRESS);
        verify(taskRepository).save(existingTask);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentTask() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.update(99L, existingTask))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(taskRepository, never()).save(any());
    }

    @Test
    void shouldDeleteExistingTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));

        taskService.delete(1L);

        verify(taskRepository, times(1)).delete(existingTask);
    }

    @Test
    void shouldChangeTaskStatus() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task result = taskService.updateStatus(1L, Status.DONE);

        assertThat(result.getStatus()).isEqualTo(Status.DONE);
    }

    @Test
    void shouldReturnTasksMatchingTitlePrefix() {
        when(taskRepository.findByTitleStartingWithIgnoreCase("fix"))
                .thenReturn(List.of(existingTask));

        List<Task> results = taskService.findByTitle("fix");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("Fix login bug");
    }
}