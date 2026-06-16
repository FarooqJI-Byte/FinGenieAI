package com.fingenie.ai.dto;

import com.fingenie.ai.enums.Role;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminUserResponse {

    private Long userId;
    private String name;
    private String email;
    private Role role;
    private Double balance;
    private boolean verified;
}