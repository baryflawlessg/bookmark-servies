package com.bookverse.repository;

import com.bookverse.entity.Book;
import com.bookverse.entity.BookGenre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookGenreRepository extends JpaRepository<BookGenre, Long> {

    // Find all genres for a book
    List<BookGenre> findByBookId(Long bookId);

    // Paginated books by a single genre
    @Query("SELECT DISTINCT b FROM Book b JOIN b.genres g WHERE g.genre = :genre")
    Page<Book> findBooksByGenre(@Param("genre") BookGenre.Genre genre, Pageable pageable);

    // Paginated books by multiple genres (ANY match)
    @Query("SELECT DISTINCT b FROM Book b JOIN b.genres g WHERE g.genre IN :genres")
    Page<Book> findBooksByGenres(@Param("genres") List<BookGenre.Genre> genres, Pageable pageable);

    // Count books by a genre
    @Query("SELECT COUNT(DISTINCT b) FROM Book b JOIN b.genres g WHERE g.genre = :genre")
    long countBooksByGenre(@Param("genre") BookGenre.Genre genre);

    // Popular genres by book count (top-N via Pageable size)
    @Query("SELECT g.genre as genre, COUNT(DISTINCT g.book.id) as cnt FROM BookGenre g GROUP BY g.genre ORDER BY cnt DESC")
    Page<Object[]> findTopGenres(Pageable pageable);
}
