package com.bookverse.service.impl;

import com.bookverse.dto.FavoriteDTO;
import com.bookverse.entity.Book;
import com.bookverse.entity.Favorite;
import com.bookverse.entity.User;
import com.bookverse.repository.BookRepository;
import com.bookverse.repository.FavoriteRepository;
import com.bookverse.repository.UserRepository;
import com.bookverse.service.FavoriteService;
import com.bookverse.service.mapper.EntityMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public FavoriteDTO addFavorite(Long userId, Long bookId) {
        if (favoriteRepository.existsByUserIdAndBookId(userId, bookId)) {
            throw new IllegalStateException("Already favorited");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setBook(book);
        
        Favorite saved = favoriteRepository.save(favorite);
        return EntityMapper.toFavoriteDTO(saved);
    }

    @Override
    @Transactional
    public void removeFavorite(Long userId, Long bookId) {
        favoriteRepository.deleteByUserIdAndBookId(userId, bookId);
    }

    @Override
    public List<FavoriteDTO> getFavorites(Long userId) {
        return favoriteRepository.findByUserIdWithBook(userId).stream()
                .map(EntityMapper::toFavoriteDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isFavorited(Long userId, Long bookId) {
        return favoriteRepository.existsByUserIdAndBookId(userId, bookId);
    }
}
