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
public class RecommendationDTO {
    private String type; // top-rated, user-genre-based, popular
    private String title; // display title
    private String description; // explanation of why these books are recommended
    private List<BookDTO> books;
}
