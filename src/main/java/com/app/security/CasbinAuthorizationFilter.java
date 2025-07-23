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
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CasbinAuthorizationFilter extends OncePerRequestFilter {

    private final Enforcer enforcer;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Skip the filter if no policies exist
        if (enforcer.getPolicy().isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        // Get the requested path and method
        String path = request.getRequestURI();
        String method = request.getMethod();

        // Skip the filter for permit-all paths (like /api/auth/**)
        if (isPermitAllPath(path, method)) {
            filterChain.doFilter(request, response);  // Allow access without authorization
            return;
        }

        // Proceed with normal authorization logic
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof User user) {
            // Allow all access for super admin
            if (isSuperAdmin(user)) {
                filterChain.doFilter(request, response);
                return;
            }

            // Check if role-based or user-specific grouping policy allows access
            if (hasRoleBasedAccess(user, path, method) || hasUserGroupingAccess(user, path, method)) {
                filterChain.doFilter(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied by Casbin");
            }
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not authenticated");
        }
    }

    // Method to check if the user is a super admin
    private boolean isSuperAdmin(User user) {
        return user.getRoles().stream().anyMatch(role -> role.name().equalsIgnoreCase("SUPER_ADMIN"));
    }

    // Method to check if role-based policy allows access
    private boolean hasRoleBasedAccess(User user, String path, String method) {
        // Iterate over the user's roles and check against the policy
        for (UserRole role : user.getRoles()) {
            if (enforcer.enforce(role.name(), path, method)) {
                return true;
            }
        }
        return false;
    }

    // Method to check if user-specific grouping policy (ptype = g) allows access
    private boolean hasUserGroupingAccess(User user, String path, String method) {
        // Iterate over each grouping policy
        for (List<String> policy : enforcer.getGroupingPolicy()) {
            // Check if the user's email is present in the policy
            boolean containsUserEmail = policy.contains(user.getEmail());

            // Debugging output to check if email is in the policy
            if (containsUserEmail) {
                System.out.println("Policy contains user email: " + user.getEmail());
            }

            // Manually check the subject, object, and action manually in the policy
            String policyEmail = policy.get(0); // Assuming email is the first entry in the grouping policy
            String policyPath = policy.size() > 1 ? policy.get(1) : ""; // Assuming path is the second entry
            String policyMethod = policy.size() > 2 ? policy.get(2) : ""; // Assuming method is the third entry

            // Debug output to check the policy values
            System.out.println("Checking policy for email: " + policyEmail + ", path: " + policyPath + ", method: " + policyMethod);

            // Manually check if the policy matches the subject, object, and action
            boolean matchesPolicy = policyEmail.equals(user.getEmail())
                    && policyPath.equals(path)
                    && policyMethod.equals(method);

            if (matchesPolicy) {
                System.out.println("Policy matches: " + user.getEmail() + " has access to " + path + " with method " + method);
                return true; // If policy matches, return true
            }
        }

        // If no policy matched, deny access
        System.out.println("No matching policy found for user: " + user.getEmail() + " on path: " + path + " with method: " + method);
        return false;
    }

    // Method to check if the path is permit-all (public) using Casbin's enforcement rules
    private boolean isPermitAllPath(String path, String method) {
        // Enforce Casbin policy for public access (sub = *, path = /api/auth/**, act = *)
        return enforcer.enforce("*", path, method);  // '*' means any user (public)
    }
}
