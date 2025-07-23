package com.app.controller;

import com.app.model.User;
import com.app.payload.request.LoginRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.payload.response.LoginResponseDto;
import com.app.security.JwtTokenService;
import com.app.service.UserService;
import com.app.service.dto.UserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        String emailOrPhone = loginRequest.getEmail() != null ? loginRequest.getEmail() : loginRequest.getPhone();
        User user = userService.findUserByEmailOrPhone(emailOrPhone);

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new AccessDeniedException("Invalid credentials");
        }
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), loginRequest.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenService.generateToken(user.getEmail());
        LoginResponseDto responseDto = new LoginResponseDto(token, "Bearer");

        return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", responseDto));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> register(@RequestBody UserDto user) {
        UserDto createdUser = userService.registerUser(user);
        return  ResponseEntity.ok(new ApiResponse<>(true, "User registered successfully", createdUser));
    }
    @GetMapping("/health")
    public String healthCheck(){
        return " Hi, it's working!";
    }
}