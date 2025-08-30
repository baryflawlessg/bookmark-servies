package com.bookverse.repository;

import com.bookverse.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // Find favorites by user
    Page<Favorite> findByUserId(Long userId, Pageable pageable);
    
    // Find favorites by book
    Page<Favorite> findByBookId(Long bookId, Pageable pageable);
    
    // Check if user has favorited a book
    boolean existsByUserIdAndBookId(Long userId, Long bookId);
    
    // Find specific favorite
    Optional<Favorite> findByUserIdAndBookId(Long userId, Long bookId);
    
    // Delete favorite by user and book
    void deleteByUserIdAndBookId(Long userId, Long bookId);
    
    // Find favorites with book details
    @Query("SELECT f FROM Favorite f LEFT JOIN FETCH f.book WHERE f.user.id = :userId")
    List<Favorite> findByUserIdWithBook(@Param("userId") Long userId);
    
    // Count favorites by book
    long countByBookId(Long bookId);
    
    // Count favorites by user
    long countByUserId(Long userId);
    
    // Find most favorited books
    @Query("SELECT f.book.id, COUNT(f) as count FROM Favorite f GROUP BY f.book.id ORDER BY count DESC")
    Page<Object[]> findMostFavoritedBooks(Pageable pageable);
}
