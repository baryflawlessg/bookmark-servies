package com.bookverse.dto;

import com.bookverse.entity.BookGenre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteriaDTO {
    private String query; // title or author
    private String author; // specific author search
    private List<BookGenre.Genre> genres;
    private Integer minYear;
    private Integer maxYear;
    private Double minRating;
    private String sortBy; // title, author, rating, date
    private String sortDirection; // asc, desc
    private Integer page;
    private Integer size;
}
