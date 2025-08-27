package com.bookverse.repository;

import com.bookverse.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.reviews WHERE u.id = :userId")
    Optional<User> findByIdWithReviews(@Param("userId") Long userId);
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.favorites f LEFT JOIN FETCH f.book WHERE u.id = :userId")
    Optional<User> findByIdWithFavorites(@Param("userId") Long userId);
}
