package com.saramageste.taskboard.task.service;

import com.saramageste.taskboard.task.dto.TaskRequestDTO;
import com.saramageste.taskboard.task.dto.TaskResponseDTO;
import com.saramageste.taskboard.task.entity.Task;
import com.saramageste.taskboard.task.enums.Status;
import com.saramageste.taskboard.task.exception.ResourceNotFoundException;
import com.saramageste.taskboard.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskResponseDTO create(TaskRequestDTO dto) {
        log.info("Creating task with title='{}', assignee='{}', status={}",
                dto.getTitle(), dto.getAssignee(), dto.getStatus());

        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setAssignee(dto.getAssignee());
        task.setPriority(dto.getPriority());
        task.setStatus(dto.getStatus());

        Task saved = taskRepository.save(task);
        log.info("Task created successfully with id={}", saved.getId());

        return toResponse(saved);
    }

    private TaskResponseDTO toResponse(Task task) {
        TaskResponseDTO dto = new TaskResponseDTO();

        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setAssignee(task.getAssignee());
        dto.setPriority(task.getPriority());
        dto.setStatus(task.getStatus());

        return dto;
    }

    public List<Task> findAll() {
        log.debug("Fetching all tasks");
        List<Task> tasks = taskRepository.findAll();
        log.debug("Found {} tasks", tasks.size());

        return tasks;
    }

    public Task findById(Long id) {
        log.debug("Fetching task with id={}", id);
        return taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Task not found with id={}", id);
                    return new ResourceNotFoundException("Task not found with id: " + id);
                });
    }

    public List<Task> findByTitle(String title) {
        log.debug("Searching tasks with title starting with '{}'", title);
        List<Task> results = taskRepository.findByTitleStartingWithIgnoreCase(title);
        log.debug("Found {} tasks matching title='{}'", results.size(), title);

        return results;
    }

    public Task update(Long id, Task updatedTask) {
        log.info("Updating task id={}", id);
        Task existing = findById(id);

        existing.setTitle(updatedTask.getTitle());
        existing.setDescription(updatedTask.getDescription());
        existing.setAssignee(updatedTask.getAssignee());
        existing.setPriority(updatedTask.getPriority());
        existing.setStatus(updatedTask.getStatus());

        Task saved =  taskRepository.save(existing);
        log.info("Task id={} updated successfully", saved.getId());

        return saved;
    }

    public void delete(Long id) {
        log.info("Deleting task id={}", id);
        Task existing = findById(id);
        taskRepository.delete(existing);
        log.info("Task id={} deleted successfully", id);
    }

    public Task updateStatus(Long id, Status status) {
        Task task = findById(id);
        log.info("Changing status of task id={} from {} to {}", id, task.getStatus(), status);

        task.setStatus(status);
        Task saved =  taskRepository.save(task);
        log.info("Task id={} status updated to {}", id, saved.getStatus());

        return saved;
    }

    public List<Task> findByStatus(Status status) {
        log.debug("Fetching tasks with status={}", status);
        return taskRepository.findByStatus(status);
    }

    public List<Task> searchByTitle(String title) {
        return taskRepository.findByTitleStartingWithIgnoreCase(title);
    }

}
