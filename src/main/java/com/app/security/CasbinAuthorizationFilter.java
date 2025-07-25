package com.app.security;

import com.app.model.User;
import com.app.model.UserRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.casbin.jcasbin.main.Enforcer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CasbinAuthorizationFilter extends OncePerRequestFilter {

    private final Enforcer enforcer;

    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
            "/api/auth/**",
            "/api/swagger-ui/**",
            "/api/swagger-ui.html",
            "/api/api-docs/**"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (enforcer.getPolicy().isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();
        String method = request.getMethod();


        if (isPermitAllPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof User user) {
            // Allow all access for super admin
            if (isSuperAdmin(user)) {
                filterChain.doFilter(request, response);
                return;
            }


            if (hasRoleBasedAccess(user, path, method) || hasUserGroupingAccess(user, path, method)) {
                filterChain.doFilter(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied by Casbin");
            }
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not authenticated");
        }
    }

    private boolean isSuperAdmin(User user) {
        return user.getRoles().stream().anyMatch(role -> role.name().equalsIgnoreCase("SUPER_ADMIN"));
    }

    private boolean hasRoleBasedAccess(User user, String path, String method) {

        for (UserRole role : user.getRoles()) {
            if (enforcer.enforce(role.name(), path, method)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasUserGroupingAccess(User user, String path, String method) {
        for (List<String> policy : enforcer.getGroupingPolicy()) {
            boolean containsUserEmail = policy.contains(user.getEmail());
            if (containsUserEmail) {
                log.debug("Policy contains user email: " + user.getEmail());
            }

            String policyEmail = policy.get(0);
            String policyPath = policy.size() > 1 ? policy.get(1) : "";
            String policyMethod = policy.size() > 2 ? policy.get(2) : "";


            log.debug("Checking policy for email: " + policyEmail + ", path: " + policyPath + ", method: " + policyMethod);


            boolean matchesPolicy = policyEmail.equals(user.getEmail())
                    && policyPath.equals(path)
                    && policyMethod.equals(method);

            if (matchesPolicy) {
                System.out.println("Policy matches: " + user.getEmail() + " has access to " + path + " with method " + method);
                return true;
            }
        }

        log.debug("No matching policy found for user: " + user.getEmail() + " on path: " + path + " with method: " + method);
        return false;
    }

    private boolean isPermitAllPath(String path) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return PUBLIC_ENDPOINTS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }
}
