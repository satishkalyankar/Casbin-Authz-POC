package com.app.controller;

import com.app.model.User;
import com.app.payload.response.ApiResponse;
import com.app.security.CurrentUser;
import com.app.service.UserService;
import com.app.service.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/users")
@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser(@CurrentUser User currentUser) {
        UserDto userDto = userService.getUserById(currentUser.getId());
        return ResponseEntity.ok(new ApiResponse<>(true, "User retrieved successfully", userDto));
    }
}
