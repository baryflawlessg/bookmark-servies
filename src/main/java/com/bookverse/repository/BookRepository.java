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
public interface BookRepository extends JpaRepository<Book, Long> {

    // Search by title or author
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(b.author) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Book> findByTitleOrAuthorContainingIgnoreCase(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Find by genre
    @Query("SELECT DISTINCT b FROM Book b JOIN b.genres g WHERE g.genre = :genre")
    Page<Book> findByGenre(@Param("genre") BookGenre.Genre genre, Pageable pageable);
    
    // Find by published year range
    Page<Book> findByPublishedYearBetween(Integer startYear, Integer endYear, Pageable pageable);
    
    // Find top rated books
    @Query("SELECT b FROM Book b LEFT JOIN b.reviews r GROUP BY b ORDER BY AVG(r.rating) DESC NULLS LAST")
    Page<Book> findTopRatedBooks(Pageable pageable);
    
    // Find books with reviews count
    @Query("SELECT b FROM Book b LEFT JOIN b.reviews r GROUP BY b ORDER BY COUNT(r) DESC")
    Page<Book> findMostReviewedBooks(Pageable pageable);
    
    // Find books by multiple genres
    @Query("SELECT DISTINCT b FROM Book b JOIN b.genres g WHERE g.genre IN :genres")
    Page<Book> findByGenres(@Param("genres") List<BookGenre.Genre> genres, Pageable pageable);
    
    // Find books with average rating above threshold
    @Query("SELECT b FROM Book b LEFT JOIN b.reviews r GROUP BY b HAVING AVG(r.rating) >= :minRating")
    Page<Book> findByAverageRatingGreaterThanEqual(@Param("minRating") Double minRating, Pageable pageable);
    
    // Find books with full details (including reviews and genres)
    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.reviews r LEFT JOIN FETCH b.genres g WHERE b.id = :bookId")
    Book findByIdWithDetails(@Param("bookId") Long bookId);
}
