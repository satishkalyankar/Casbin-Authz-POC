package com.app.payload.request;

import lombok.Data;

@Data
public class PermitAllPolicyRequest {
    private String path;  // The path to be publicly accessible (e.g., /api/auth/**)
    private String method; // The HTTP method (e.g., GET, POST)
}
