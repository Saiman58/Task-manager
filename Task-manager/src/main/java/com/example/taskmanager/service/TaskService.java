package com.example.taskmanager.service;

import com.example.taskmanager.dto.CreateTaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.dto.UpdateStatusRequest;
import org.springframework.data.domain.Page;


public interface TaskService {

    public Page<TaskResponse> getAllTasks (int page, int size);

    public TaskResponse getTaskById (Long id);

    public TaskResponse createTask (CreateTaskRequest request);

    public TaskResponse assignTask(Long taskId, Long userId);

    public TaskResponse changeStatus(Long taskId, UpdateStatusRequest request);


}
