package com.bookverse.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews", indexes = {
    @Index(name = "idx_review_book_id", columnList = "book_id"),
    @Index(name = "idx_review_user_id", columnList = "user_id"),
    @Index(name = "idx_review_created_at", columnList = "created_at"),
    @Index(name = "idx_review_rating", columnList = "rating")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    @Column(nullable = false)
    private Integer rating;

    @NotBlank(message = "Review text is required")
    @Size(min = 10, max = 2000, message = "Review text must be between 10 and 2000 characters")
    @Column(name = "review_text", columnDefinition = "TEXT", nullable = false)
    private String reviewText;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Helper methods
    public void setBook(Book book) {
        this.book = book;
        if (book != null && !book.getReviews().contains(this)) {
            book.addReview(this);
        }
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null && !user.getReviews().contains(this)) {
            user.addReview(this);
        }
    }

    // Business methods
    public boolean isOwnedBy(Long userId) {
        return user != null && user.getId().equals(userId);
    }
}
