package com.app.service.dto;

import com.app.model.UserRole;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class PolicyRequest {
    @Enumerated(EnumType.STRING)
    private UserRole role;    // subject (e.g., admin)
    private String path;    // object (e.g., /users/me)
    private String method;  // action (e.g., GET)
}
