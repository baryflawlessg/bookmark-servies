package com.bookverse.service;

import com.bookverse.dto.FavoriteDTO;

import java.util.List;

public interface FavoriteService {
    FavoriteDTO addFavorite(Long userId, Long bookId);
    void removeFavorite(Long userId, Long bookId);
    List<FavoriteDTO> getFavorites(Long userId);
    boolean isFavorited(Long userId, Long bookId);
}
