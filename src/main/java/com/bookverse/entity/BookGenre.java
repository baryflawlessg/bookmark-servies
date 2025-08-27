package com.bookverse.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "book_genres", indexes = {
    @Index(name = "idx_book_genre_book_id", columnList = "book_id"),
    @Index(name = "idx_book_genre_genre", columnList = "genre")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookGenre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @NotNull(message = "Genre is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Genre genre;

    // Genre enum
    public enum Genre {
        FANTASY,
        SCI_FI,
        MYSTERY,
        THRILLER,
        ROMANCE,
        FICTION,
        NON_FICTION,
        BIOGRAPHY,
        HISTORY,
        SCIENCE,
        TECHNOLOGY,
        PHILOSOPHY,
        POETRY,
        DRAMA,
        HORROR,
        ADVENTURE,
        CRIME,
        HUMOR,
        CHILDREN,
        YOUNG_ADULT
    }

    // Helper methods
    public void setBook(Book book) {
        this.book = book;
        if (book != null && !book.getGenres().contains(this)) {
            book.addGenre(this);
        }
    }
}
