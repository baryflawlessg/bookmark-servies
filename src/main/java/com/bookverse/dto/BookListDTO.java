package com.bookverse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookListDTO {
    private Long id;
    private String title;
    private String author;
    private String coverImageUrl;
    private Double averageRating;
}
