package com.app.security;

import com.app.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtAuthenticationService {

    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenService jwtTokenService;

    public String authenticateAndGenerateToken(User user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        return jwtTokenService.generateToken(userDetails.getUsername());
    }
}
