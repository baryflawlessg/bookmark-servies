package com.bookverse.repository;

import com.bookverse.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Find reviews by book
    Page<Review> findByBookIdOrderByCreatedAtDesc(Long bookId, Pageable pageable);
    
    // Find reviews by user
    Page<Review> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    // Find reviews by book and user
    List<Review> findByBookIdAndUserId(Long bookId, Long userId);
    
    // Find reviews by rating
    Page<Review> findByRating(Integer rating, Pageable pageable);
    
    // Find recent reviews
    @Query("SELECT r FROM Review r ORDER BY r.createdAt DESC")
    Page<Review> findRecentReviews(Pageable pageable);
    
    // Find reviews with book details
    @Query("SELECT r FROM Review r LEFT JOIN FETCH r.book WHERE r.id = :reviewId")
    Review findByIdWithBook(@Param("reviewId") Long reviewId);
    
    // Find reviews with user details
    @Query("SELECT r FROM Review r LEFT JOIN FETCH r.user WHERE r.id = :reviewId")
    Review findByIdWithUser(@Param("reviewId") Long reviewId);
    
    // Count reviews by book
    long countByBookId(Long bookId);
    
    // Count reviews by user
    long countByUserId(Long userId);
    
    // Average rating by book
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book.id = :bookId")
    Double getAverageRatingByBookId(@Param("bookId") Long bookId);
}
