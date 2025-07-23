package com.app.model;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorProvider")
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()) {
            String username = auth.getName();

            if (!"anonymousUser".equalsIgnoreCase(username)) {
                return Optional.of(username);
            } else {
                return Optional.of("ADMIN");
            }
        }

        return Optional.of("ADMIN");
    }
}

