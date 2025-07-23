package com.app.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginRequestDto {
    @Email(message = "Must be a well-formed email address")
    private String email;

    @Pattern(regexp = "^\\+?\\d{10,15}$", message = "Phone number must be between 10 and 15 digits, including optional '+' sign.")
    private String phone;

    @NotBlank(message = "Password is required")
    private String password;
}
