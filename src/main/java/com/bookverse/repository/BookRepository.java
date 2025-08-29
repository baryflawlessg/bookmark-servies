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

    @Query("SELECT DISTINCT b FROM Book b " +
           "LEFT JOIN b.genres g " +
           "WHERE (:title IS NULL OR :title = '' OR b.title LIKE %:title%) " +
           "AND (:minYear IS NULL OR b.publishedYear >= :minYear) " +
           "AND (:maxYear IS NULL OR b.publishedYear <= :maxYear) " +
           "AND (:minRating IS NULL OR COALESCE(b.averageRating, 0.0) >= :minRating) " +
           "AND (:genres IS NULL OR g.genre IN :genres)")
    Page<Book> findBooks(
        @Param("title") String title,
        @Param("genres") List<BookGenre.Genre> genres,
        @Param("minYear") Integer minYear,
        @Param("maxYear") Integer maxYear,
        @Param("minRating") Double minRating,
        Pageable pageable);
}
