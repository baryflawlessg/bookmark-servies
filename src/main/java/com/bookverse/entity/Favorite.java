package com.bookverse.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "favorites", indexes = {
    @Index(name = "idx_favorite_user_id", columnList = "user_id"),
    @Index(name = "idx_favorite_book_id", columnList = "book_id"),
    @Index(name = "idx_favorite_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Favorite {

    @EmbeddedId
    private FavoriteId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("bookId")
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Helper methods
    public void setUser(User user) {
        this.user = user;
        if (this.id == null) {
            this.id = new FavoriteId();
        }
        this.id.setUserId(user.getId());
        if (user != null && !user.getFavorites().contains(this)) {
            user.addFavorite(this);
        }
    }

    public void setBook(Book book) {
        this.book = book;
        if (this.id == null) {
            this.id = new FavoriteId();
        }
        this.id.setBookId(book.getId());
        if (book != null && !book.getFavorites().contains(this)) {
            book.addFavorite(this);
        }
    }

    // Embedded ID class for composite primary key
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FavoriteId {
        @Column(name = "user_id")
        private Long userId;

        @Column(name = "book_id")
        private Long bookId;
    }
}
