package com.example.taskmanager.dto;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;
import lombok.Data;

@Data
public class TaskResponse {
    private Long id;
    private String name;
    private String description;
    private TaskStatus status;
    private UserBriefResponse assignee;


    //для создания ответа из задачи
    public static TaskResponse from(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setName(task.getName());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());

        if (task.getAssignee() != null) {
            response.setAssignee(UserBriefResponse.from(task.getAssignee()));
        }

        return response;
    }
}
