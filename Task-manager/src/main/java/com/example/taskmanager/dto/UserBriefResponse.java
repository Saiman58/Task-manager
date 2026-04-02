package com.example.taskmanager.dto;

import com.example.taskmanager.model.User;
import lombok.Data;

//сокращенная инфа. о пользователе
@Data
public class UserBriefResponse {
    private Long id;
    private String name;
    private String email;

    public static UserBriefResponse from(User user) {
        UserBriefResponse response = new UserBriefResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        return response;
    }
}
