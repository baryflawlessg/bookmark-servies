package com.bookverse.service.impl;

import com.bookverse.dto.PageResponse;
import com.bookverse.dto.ReviewCreateDTO;
import com.bookverse.dto.ReviewDTO;
import com.bookverse.dto.ReviewUpdateDTO;
import com.bookverse.entity.Book;
import com.bookverse.entity.Review;
import com.bookverse.entity.User;
import com.bookverse.repository.BookRepository;
import com.bookverse.repository.ReviewRepository;
import com.bookverse.repository.UserRepository;
import com.bookverse.service.ReviewService;
import com.bookverse.service.mapper.EntityMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ReviewDTO createReview(Long bookId, Long userId, ReviewCreateDTO request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Review review = new Review();
        review.setBook(book);
        review.setUser(user);
        review.setRating(request.getRating());
        review.setReviewText(request.getReviewText());

        Review saved = reviewRepository.save(review);
        
        // Update book's rating stats after adding review
        book.addReview(saved);
        book.updateRatingStats();
        bookRepository.save(book);
        
        return EntityMapper.toReviewDTO(saved);
    }

    @Override
    @Transactional
    public ReviewDTO updateReview(Long reviewId, Long userId, ReviewUpdateDTO request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        if (!review.isOwnedBy(userId)) {
            throw new SecurityException("Cannot edit a review you do not own");
        }
        
        Book book = review.getBook();
        review.setRating(request.getRating());
        review.setReviewText(request.getReviewText());
        Review saved = reviewRepository.save(review);
        
        // Update book's rating stats after updating review
        book.updateRatingStats();
        bookRepository.save(book);
        
        return EntityMapper.toReviewDTO(saved);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        if (!review.isOwnedBy(userId)) {
            throw new SecurityException("Cannot delete a review you do not own");
        }
        
        Book book = review.getBook();
        reviewRepository.delete(review);
        
        // Update book's rating stats after deleting review
        book.updateRatingStats();
        bookRepository.save(book);
    }

    @Override
    public PageResponse<ReviewDTO> getReviewsForBook(Long bookId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> result = reviewRepository.findByBookIdOrderByCreatedAtDesc(bookId, pageable);
        List<ReviewDTO> items = result.getContent().stream().map(EntityMapper::toReviewDTO).collect(Collectors.toList());
        return EntityMapper.toPageResponse(items, page, size, result.getTotalElements());
    }

    @Override
    public PageResponse<ReviewDTO> getReviewsForUser(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> result = reviewRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        List<ReviewDTO> items = result.getContent().stream().map(EntityMapper::toReviewDTO).collect(Collectors.toList());
        return EntityMapper.toPageResponse(items, page, size, result.getTotalElements());
    }
}
