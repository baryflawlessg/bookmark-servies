package com.bookverse.service.mapper;

import com.bookverse.dto.*;
import com.bookverse.entity.Book;
import com.bookverse.entity.BookGenre;
import com.bookverse.entity.Favorite;
import com.bookverse.entity.Review;
import com.bookverse.entity.User;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class EntityMapper {
    private EntityMapper() {}

    public static UserDTO toUserDTO(User user) {
        if (user == null) return null;
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public static BookDTO toBookDTO(Book book) {
        if (book == null) return null;
        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .coverImageUrl(book.getCoverImageUrl())
                .publishedYear(book.getPublishedYear())
                .averageRating(book.getAverageRating())
                .reviewCount(book.getReviewCount())
                .genres(safeGenres(book))
                .build();
    }

    public static BookDetailDTO toBookDetailDTO(Book book) {
        if (book == null) return null;
        return BookDetailDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .description(book.getDescription())
                .coverImageUrl(book.getCoverImageUrl())
                .publishedYear(book.getPublishedYear())
                .averageRating(book.getAverageRating())
                .reviewCount(book.getReviewCount())
                .genres(safeGenres(book))
                .build();
    }

    public static ReviewDTO toReviewDTO(Review review) {
        if (review == null) return null;
        return ReviewDTO.builder()
                .id(review.getId())
                .bookId(review.getBook() != null ? review.getBook().getId() : null)
                .userId(review.getUser() != null ? review.getUser().getId() : null)
                .userName(review.getUser() != null ? review.getUser().getName() : null)
                .rating(review.getRating())
                .reviewText(review.getReviewText())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    public static FavoriteDTO toFavoriteDTO(Favorite favorite) {
        if (favorite == null) return null;
        return FavoriteDTO.builder()
                .id(favorite.getId())
                .userId(favorite.getUser() != null ? favorite.getUser().getId() : null)
                .bookId(favorite.getBook() != null ? favorite.getBook().getId() : null)
                .bookTitle(favorite.getBook() != null ? favorite.getBook().getTitle() : null)
                .bookAuthor(favorite.getBook() != null ? favorite.getBook().getAuthor() : null)
                .coverImageUrl(favorite.getBook() != null ? favorite.getBook().getCoverImageUrl() : null)
                .createdAt(favorite.getCreatedAt())
                .build();
    }

    private static List<String> safeGenres(Book book) {
        if (book.getGenres() == null) return List.of();
        return book.getGenres().stream()
                .map(BookGenre::getGenre)
                .filter(Objects::nonNull)
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    public static <T> PageResponse<T> toPageResponse(List<T> items, int page, int size, long total) {
        int totalPages = (int) Math.ceil(total / (double) size);
        PaginationDTO pagination = PaginationDTO.builder()
                .page(page)
                .size(size)
                .totalElements(total)
                .totalPages(totalPages)
                .first(page == 0)
                .last(page + 1 >= totalPages)
                .build();
        return PageResponse.<T>builder().items(items).pagination(pagination).build();
    }
}
