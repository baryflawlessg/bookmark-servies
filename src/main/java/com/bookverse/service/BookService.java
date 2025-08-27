package com.bookverse.service;

import com.bookverse.dto.BookDetailDTO;
import com.bookverse.dto.BookDTO;
import com.bookverse.dto.PageResponse;
import com.bookverse.dto.ReviewDTO;
import com.bookverse.dto.SearchCriteriaDTO;

import java.util.List;

public interface BookService {
    PageResponse<BookDTO> searchBooks(SearchCriteriaDTO criteria);
    BookDetailDTO getBookDetails(Long bookId);
    PageResponse<ReviewDTO> getBookReviews(Long bookId, Integer page, Integer size);
    List<BookDTO> getFeaturedBooks();
}
