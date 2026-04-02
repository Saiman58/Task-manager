package com.example.taskmanager.controller;

import com.example.taskmanager.dto.CreateTaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.dto.UpdateStatusRequest;
import com.example.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    //GET /api/tasks?page=0&size=10 задачи с плагинацией
    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TaskResponse> tasks = taskService.getAllTasks(page, size);
        return ResponseEntity.ok(tasks);
    }

    //GET /api/tasks/1 получить таску по id
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        TaskResponse task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    //POST/api/tasks создание таски
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request) {
        TaskResponse createdTask = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    //PUT/api/tasks/1/assign/5 назначить исполнителя
    @PutMapping("/{taskId}/assign/{userId}")
    public ResponseEntity<TaskResponse> assignTask(@PathVariable Long taskId,
                                                   @PathVariable Long userId) {
        TaskResponse task = taskService.assignTask(taskId, userId);
        return ResponseEntity.ok(task);
    }

    //PATCH /api/tasks/1/status изменить статус задачи
    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskResponse> changeStatus(@PathVariable Long taskId,
                                                     @Valid @RequestBody UpdateStatusRequest request) {
        TaskResponse task = taskService.changeStatus(taskId, request);
        return ResponseEntity.ok(task);
    }
}
