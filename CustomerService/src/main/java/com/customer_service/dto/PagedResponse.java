package com.customer_service.dto;

import java.io.Serializable;
import java.util.List;

public record PagedResponse<T>(List<T> content,
                               int pageNumber,
                               int pageSize,
                               long totalElements,
                               int totalPages,
                               boolean last)
        implements Serializable {
}
