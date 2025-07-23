package com.app.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * A wrapper for paginated API responses.
 * Used as the data payload in ApiResponse for endpoints returning paginated collections.
 *
 * @param <T> The type of content in the paginated list
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {

    /**
     * The content items for the current page
     */
    private List<T> content;

    /**
     * Current page number (0-indexed)
     */
    private int page;

    /**
     * Number of items per page
     */
    private int size;

    /**
     * Total number of items across all pages
     */
    private long totalElements;

    /**
     * Total number of pages
     */
    private int totalPages;

    /**
     * Whether this is the last page
     */
    private boolean last;
}
