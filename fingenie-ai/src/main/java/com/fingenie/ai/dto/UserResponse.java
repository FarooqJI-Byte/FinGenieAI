package com.fingenie.ai.dto;

import lombok.*;

@Getter
@Setter
@Builder
public class UserResponse {

    private Long userId;
    private String name;
    private String email;
    private String role;
}