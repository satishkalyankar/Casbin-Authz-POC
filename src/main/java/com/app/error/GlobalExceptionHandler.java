package com.app.error;

import com.app.payload.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ApiResponse errorResponse = new ApiResponse();
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setSuccess(false);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(BadRequestAlertException.class)
    public ResponseEntity<ApiResponse> handleBadRequestAlertException(BadRequestAlertException ex) {
        ApiResponse errorResponse = new ApiResponse();
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setSuccess(false);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGeneralException(Exception ex) {
        ApiResponse errorResponse = new ApiResponse();
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setSuccess(false);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDeniedException(AccessDeniedException ex) {
        ApiResponse errorResponse = new ApiResponse();
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setSuccess(false);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
}