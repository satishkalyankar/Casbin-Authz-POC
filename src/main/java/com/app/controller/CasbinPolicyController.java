package com.app.controller;
import com.app.error.BadRequestAlertException;
import com.app.model.User;
import com.app.payload.request.GroupPolicyRequest;
import com.app.payload.request.PermitAllPolicyRequest;
import com.app.payload.response.ApiResponse;
import com.app.repo.UserRepository;
import com.app.service.dto.PolicyRequest;
import lombok.RequiredArgsConstructor;
import org.casbin.jcasbin.main.Enforcer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/policies")
@RequiredArgsConstructor
public class CasbinPolicyController {

    private final Enforcer enforcer;
    private final UserRepository userRepository;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<String>> addPolicy(@RequestBody PolicyRequest request) {
        boolean added = enforcer.addPolicy(request.getRole().name(), request.getPath(), request.getMethod());

        if (added) {
            enforcer.savePolicy();
            return ResponseEntity.ok(new ApiResponse<>(true, "Policy added successfully.", null));
        } else {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Policy already exists for role: "
                            + request.getRole() + ", path: " + request.getPath() + ", method: " + request.getMethod(), null));
        }
    }


    @PostMapping("/assign-role")
    public ResponseEntity<String> assignUserToRole(@RequestParam String username, @RequestParam String role) {
        boolean added = enforcer.addGroupingPolicy(username, role);
        if (added) {
            enforcer.savePolicy();
            return ResponseEntity.ok("User assigned to role.");
        } else {
            return ResponseEntity.badRequest().body("Already assigned.");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllPolicies() {
        return ResponseEntity.ok(enforcer.getPolicy());
    }

    @PostMapping("/assign-group")
    public ResponseEntity<ApiResponse<String>> assignUserToGroup(@RequestBody GroupPolicyRequest request) {
        User user = userRepository.findByEmail(request.getUsername())
                .orElseThrow(() -> new BadRequestAlertException("user not found with email :" + request.getUsername()));
        String username = request.getUsername();
        String path = request.getPath();
        String method = request.getMethod();
        boolean added = enforcer.addGroupingPolicy(username, path, method);
        if (added) {
            enforcer.savePolicy();
            return ResponseEntity.ok(new ApiResponse<>(true,"User assigned to group with permission", null));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>(true, "User is already assigned to this permission.", null));
        }
    }

    @DeleteMapping("/remove-role")
    public ResponseEntity<ApiResponse<String>> removeUserFromRole(@RequestBody PolicyRequest request) {
        boolean removed = enforcer.removePolicy(request.getRole().name(), request.getPath(),request.getMethod());
        if (removed) {
            enforcer.savePolicy();
            return ResponseEntity.ok(new ApiResponse<>(true, "User removed from role.", null));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "User is not assigned to this role.", null));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<String>> deletePolicy(@RequestBody PolicyRequest request) {
        boolean removed = enforcer.removePolicy(request.getRole().name(), request.getPath(), request.getMethod());

        if (removed) {
            enforcer.savePolicy();
            return ResponseEntity.ok(new ApiResponse<>(true, "Policy removed successfully.", null));
        } else {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Policy does not exist for role: "
                            + request.getRole() + ", path: " + request.getPath() + ", method: " + request.getMethod(), null));
        }
    }


    @GetMapping("/grouping-policies")
    public ResponseEntity<Object> getAllGroupingPolicies() {
        return ResponseEntity.ok(enforcer.getGroupingPolicy());
    }

    @PostMapping("/add-bulk")
    public ResponseEntity<ApiResponse<List<PolicyRequest>>> addBulkPolicy(@RequestBody List<PolicyRequest> requests) {
        List<PolicyRequest> failedPolicies = new ArrayList<>();


        for (PolicyRequest request : requests) {
            boolean added = enforcer.addPolicy(request.getRole().name(), request.getPath(), request.getMethod());
            if (!added) {
                failedPolicies.add(request);
            }
        }

        if (failedPolicies.isEmpty()) {
            enforcer.savePolicy();
            return ResponseEntity.ok(new ApiResponse<>(true,"All policies added successfully",requests));
        } else {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false,"Some policies were not added because they already exist: ",failedPolicies));
        }
    }

    @PostMapping("/add-permit-all")
    public ResponseEntity<ApiResponse<String>> addPermitAllPolicy(@RequestBody PermitAllPolicyRequest request) {

        boolean added = enforcer.addPolicy("*", request.getPath(), request.getMethod());

        if (added) {
            enforcer.savePolicy();
            return ResponseEntity.ok(new ApiResponse<>(true, "Permit-all policy added successfully.", null));
        } else {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Permit-all policy already exists for path: "
                            + request.getPath() + ", method: " + request.getMethod(), null));
        }
    }

    @DeleteMapping("/remove-group")
    public ResponseEntity<ApiResponse<String>> removeGroupPolicy(@RequestBody GroupPolicyRequest request) {
        String username = request.getUsername();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new BadRequestAlertException("user not found: " + username));
        String path = request.getPath();
        String method = request.getMethod();
        boolean removed = enforcer.removeGroupingPolicy(username, path, method);
        if (removed) {
            enforcer.savePolicy();
            return ResponseEntity.ok(new ApiResponse<>(true, "User's group policy removed successfully.", null));
        } else {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Group policy not found for user: " + username + ", path: " + path + ", method: " + method, null));
        }
    }

}
