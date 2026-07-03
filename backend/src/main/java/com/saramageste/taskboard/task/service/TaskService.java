package com.saramageste.taskboard.task.service;

import com.saramageste.taskboard.task.entity.Task;
import com.saramageste.taskboard.task.enums.Status;
import com.saramageste.taskboard.task.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task create(Task task) {
        return taskRepository.save(task);
    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
    }

    public Task update(Long id, Task updatedTask) {
        Task existing = findById(id);

        existing.setTitle(updatedTask.getTitle());
        existing.setDescription(updatedTask.getDescription());
        existing.setAssignee(updatedTask.getAssignee());
        existing.setPriority(updatedTask.getPriority());
        existing.setStatus(updatedTask.getStatus());

        return taskRepository.save(existing);
    }

    public void delete(Long id) {
        Task existing = findById(id);
        taskRepository.delete(existing);
    }

    public Task updateStatus(Long id, Status status) {
        Task task = findById(id);
        task.setStatus(status);
        return taskRepository.save(task);
    }

    public List<Task> findByStatus(Status status) {
        return taskRepository.findByStatus(status);
    }

    public List<Task> searchByTitle(String title) {
        return taskRepository.findByTitleContainingIgnoreCase(title);
    }

}
