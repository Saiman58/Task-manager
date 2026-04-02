package com.example.taskmanager.service;

import com.example.taskmanager.kafka.TaskEvent;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, TaskEvent> kafkaTemplate;

    private static final String TOPIC = "task-events";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    //Отправляем событие о создании задачи
    public void sendTaskCreatedEvent(Task task) {
        TaskEvent event = new TaskEvent();
        event.setEventType("TASK_CREATED");
        event.setTaskId(task.getId());
        event.setTaskName(task.getName());
        event.setNewStatus(task.getStatus());
        event.setTimestamp(LocalDateTime.now().format(FORMATTER));

        sendEvent(event);
    }

    //Отправляем событие о назначении исполнителя
    public void sendTaskAssignedEvent(Task task) {
        TaskEvent event = new TaskEvent();
        event.setEventType("TASK_ASSIGNED");
        event.setTaskId(task.getId());
        event.setTaskName(task.getName());
        event.setNewStatus(task.getStatus());
        event.setTimestamp(LocalDateTime.now().format(FORMATTER));

        if (task.getAssignee() != null) {
            event.setAssigneeId(task.getAssignee().getId());
            event.setAssigneeName(task.getAssignee().getName());
        }

        sendEvent(event);
    }

    //Отправляем событие о смене статуса
    public void sendTaskStatusChangedEvent(Task task, TaskStatus oldStatus) {
        TaskEvent event = new TaskEvent();
        event.setEventType("TASK_STATUS_CHANGED");
        event.setTaskId(task.getId());
        event.setTaskName(task.getName());
        event.setOldStatus(oldStatus);
        event.setNewStatus(task.getStatus());
        event.setTimestamp(LocalDateTime.now().format(FORMATTER));

        if (task.getAssignee() != null) {
            event.setAssigneeId(task.getAssignee().getId());
            event.setAssigneeName(task.getAssignee().getName());
        }

        sendEvent(event);
    }

    //Общий метод отправки события в Kafka
    private void sendEvent(TaskEvent event) {
        try {
            // Отправляем событие в топик
            kafkaTemplate.send(TOPIC, String.valueOf(event.getTaskId()), event);
            log.info("Событие отправлено в Kafka: {} - {}", event.getEventType(), event.getTaskId());
        } catch (Exception e) {
            log.error("Ошибка при отправке события в Kafka: {}", e.getMessage(), e);
        }
    }
}
