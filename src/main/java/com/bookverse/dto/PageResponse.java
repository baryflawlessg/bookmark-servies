package com.bookverse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> items;
    private PaginationDTO pagination;

    public static <T> PageResponse<T> of(List<T> items, PaginationDTO pagination) {
        return PageResponse.<T>builder()
                .items(items)
                .pagination(pagination)
                .build();
    }
}
