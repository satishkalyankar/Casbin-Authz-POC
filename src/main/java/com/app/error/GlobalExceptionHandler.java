package com.app.error;

import com.app.payload.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Handle IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ApiResponse errorResponse = new ApiResponse();
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setSuccess(false);
        logger.error("IllegalArgumentException occurred: ", ex); // Log the exception
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // Handle custom BadRequestAlertException
    @ExceptionHandler(BadRequestAlertException.class)
    public ResponseEntity<ApiResponse> handleBadRequestAlertException(BadRequestAlertException ex) {
        ApiResponse errorResponse = new ApiResponse();
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setSuccess(false);
        logger.error("BadRequestAlertException occurred: ", ex); // Log the exception
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // Handle general Exception (catch-all handler)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGeneralException(Exception ex) {
        ApiResponse errorResponse = new ApiResponse();
        errorResponse.setMessage("An unexpected error occurred: " + ex.getMessage());
        errorResponse.setSuccess(false);
        logger.error("General exception occurred: ", ex); // Log the exception
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    // Handle AccessDeniedException (Unauthorized access)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDeniedException(AccessDeniedException ex) {
        ApiResponse errorResponse = new ApiResponse();
        errorResponse.setMessage("Access Denied: " + ex.getMessage());
        errorResponse.setSuccess(false);
        logger.error("Access Denied: ", ex); // Log the exception
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse); // 403 Forbidden
    }
}
