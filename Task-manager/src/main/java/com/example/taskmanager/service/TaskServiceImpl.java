package com.example.taskmanager.service;

import com.example.taskmanager.dto.CreateTaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.dto.UpdateStatusRequest;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final KafkaProducerService kafkaProducerService;

    //- получение задач с пагинацией
    @Override
    public Page<TaskResponse> getAllTasks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Task> taskPage = taskRepository.findAllByOrderByIdDesc(pageable);
        return taskPage.map(TaskResponse::from);
    }

    //- получение задачи по id
    @Override
    public TaskResponse getTaskById(Long id) {
        Task task = findTaskById(id);
        return TaskResponse.from(task);
    }

    //- добавление задачи
    @Override
    public TaskResponse createTask(CreateTaskRequest request) {
        Task task = new Task(request.getName(), request.getDescription());
        Task savedTask = taskRepository.save(task);
        kafkaProducerService.sendTaskCreatedEvent(savedTask);

        return TaskResponse.from(savedTask);
    }

    //- назначение исполнителя задаче
    @Override
    public TaskResponse assignTask(Long taskId, Long userId) {
        Task task = findTaskById(taskId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден с id: " + userId));
        task.setAssignee(user);

        if (task.getStatus() == TaskStatus.CREATED){
            task.setStatus(TaskStatus.ASSIGNED);
        }

        Task updatedTask = taskRepository.save(task);
        kafkaProducerService.sendTaskAssignedEvent(updatedTask);
        return TaskResponse.from(updatedTask);
    }

    //- смена статуса задаче
    @Override
    public TaskResponse changeStatus(Long taskId, UpdateStatusRequest request) {
        Task task = findTaskById(taskId);
        TaskStatus oldStatus = task.getStatus();
        TaskStatus newStatus = request.getStatus();

        validateStatusTransition(oldStatus, newStatus);

        task.setStatus(newStatus);
        Task updatedTask = taskRepository.save(task);
        kafkaProducerService.sendTaskStatusChangedEvent(updatedTask, oldStatus);

        return TaskResponse.from(updatedTask);
    }


    /*
     ДОП. МЕТОДЫ:
    */

    //найти задачу
    public Task findTaskById(Long id){
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Задача не найдена с id: \" " + id));
    }


    //валидация перехода между статусами
    private void validateStatusTransition(TaskStatus oldStatus, TaskStatus newStatus) {
        if (oldStatus == newStatus) {
            return;
        }
        if (oldStatus == TaskStatus.CREATED && newStatus == TaskStatus.COMPLETED) {
            throw new RuntimeException("Нельзя завершить задачу, которая не была назначена");
        }
        if (oldStatus == TaskStatus.CREATED && newStatus == TaskStatus.CANCELLED) {
            throw new RuntimeException("Нельзя отменить задачу, которая не была назначена");
        }
        if (oldStatus == TaskStatus.COMPLETED || oldStatus == TaskStatus.CANCELLED) {
            throw new RuntimeException("Нельзя изменить статус завершённой или отменённой задачи");
        }
    }


}
