package com.app.service.impl;

import com.app.error.BadRequestAlertException;
import com.app.model.User;
import com.app.repo.UserRepository;
import com.app.service.UserService;
import com.app.service.dto.UserDto;
import com.app.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";


    @Override
    @Transactional
    public User findUserByEmailOrPhone(String emailOrPhone) {
        User user = userRepository.findByEmail(emailOrPhone)
                .orElseGet(() -> userRepository.findByPhone(emailOrPhone)
                        .orElseThrow(() -> new BadRequestAlertException("User not found with email or phone: " + emailOrPhone)));

        return user;
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BadRequestAlertException("User not found with id: " + id));
        return userMapper.toDto(user);
    }

    @Transactional
    public UserDto registerUser(UserDto requestDto) {
            if (!isValidEmail(requestDto.getEmail())) {
                throw new BadRequestAlertException("Invalid email format");
            }

            String normalizedPhone = normalizeAndValidatePhoneNumber(requestDto.getPhone());
            if (userRepository.existsByEmail(requestDto.getEmail())) {
                throw new BadRequestAlertException("Email already registered: " + requestDto.getEmail());
            }
            if (userRepository.existsByPhone(normalizedPhone)) {
                throw new BadRequestAlertException("Phone number already registered: " + normalizedPhone);
            }
            User createUser = userMapper.toEntity(requestDto);
            createUser.setPassword(passwordEncoder.encode(requestDto.getPassword()));
            User savedUser = userRepository.save(createUser);
            return userMapper.toDto(savedUser);
    }
    public static boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public String normalizeAndValidatePhoneNumber(String phoneNumber) {
        phoneNumber = phoneNumber.trim();
        phoneNumber = phoneNumber.replaceAll("[^\\d]", "");
        if (phoneNumber.length() == 12 && phoneNumber.startsWith("91")) {
            return "+91" + phoneNumber.substring(2);
        }
        if (phoneNumber.length() != 10) {
            throw new BadRequestAlertException("Invalid phone number. It should have exactly 10 digits.");
        }
        return "+91" + phoneNumber;
    }

    private String generateVerificationToken() {
        return UUID.randomUUID().toString();
    }
}
