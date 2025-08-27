package com.bookverse.service;

import com.bookverse.dto.PageResponse;
import com.bookverse.dto.ReviewCreateDTO;
import com.bookverse.dto.ReviewDTO;
import com.bookverse.dto.ReviewUpdateDTO;

public interface ReviewService {
    ReviewDTO createReview(Long bookId, Long userId, ReviewCreateDTO request);
    ReviewDTO updateReview(Long reviewId, Long userId, ReviewUpdateDTO request);
    void deleteReview(Long reviewId, Long userId);
    PageResponse<ReviewDTO> getReviewsForBook(Long bookId, int page, int size);
    PageResponse<ReviewDTO> getReviewsForUser(Long userId, int page, int size);
}
