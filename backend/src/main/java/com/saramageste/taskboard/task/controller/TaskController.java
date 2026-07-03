package com.saramageste.taskboard.task.controller;

import com.saramageste.taskboard.task.entity.Task;
import com.saramageste.taskboard.task.enums.Status;
import com.saramageste.taskboard.task.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<Task> create(@RequestBody Task task) {
        return ResponseEntity.ok(taskService.create(task));
    }

    @GetMapping
    public ResponseEntity<List<Task>> findAll() {
        return ResponseEntity.ok(taskService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> findById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> update(@PathVariable Long id,
                                       @RequestBody Task task) {
        return ResponseEntity.ok(taskService.update(id, task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Task> updateStatus(@PathVariable Long id,
                                             @RequestParam Status status) {
        return ResponseEntity.ok(taskService.updateStatus(id, status));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Task>> findByStatus(@PathVariable Status status) {
        return ResponseEntity.ok(taskService.findByStatus(status));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Task>> search(@RequestParam String title) {
        return ResponseEntity.ok(taskService.searchByTitle(title));
    }
}
