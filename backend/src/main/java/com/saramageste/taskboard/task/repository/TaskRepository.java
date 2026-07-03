package com.saramageste.taskboard.task.repository;

import com.saramageste.taskboard.task.entity.Task;
import com.saramageste.taskboard.task.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(Status status);
    List<Task> findByTitleStartingWithIgnoreCase(String title);
}
