package com.example.taskmanager.kafka;

import com.example.taskmanager.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskEvent {
    private String eventType;      // Тип события: TASK_CREATED, TASK_ASSIGNED, TASK_STATUS_CHANGED
    private Long taskId;           // ID задачи
    private String taskName;       // Название задачи
    private Long assigneeId;       // ID исполнителя (если есть)
    private String assigneeName;   // Имя исполнителя (если есть)
    private TaskStatus oldStatus;  // Старый статус (для смены статуса)
    private TaskStatus newStatus;  // Новый статус
    private String timestamp;      // Время события
}
