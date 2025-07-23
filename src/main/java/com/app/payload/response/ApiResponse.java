package com.app.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A generic response wrapper for all API responses.
 * Provides a standardized format for controller responses.
 *
 * @param <T> The type of data contained in the response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /**
     * Whether the request was successful or not
     */
    private boolean success;

    /**
     * A message describing the result of the request
     */
    private String message;

    /**
     * The data payload of the response (can be null for error responses or void operations)
     */
    private T data;
}
