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
public class BookDTO {
    private Long id;
    private String title;
    private String author;
    private String coverImageUrl;
    private Integer publishedYear;
    private Double averageRating;
    private Integer reviewCount;
    private List<String> genres; // names as strings
}
