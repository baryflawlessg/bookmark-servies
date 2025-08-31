package com.bookverse.service.impl;

import com.bookverse.dto.BookDTO;
import com.bookverse.dto.RecommendationDTO;
import com.bookverse.entity.Book;
import com.bookverse.entity.BookGenre;
import com.bookverse.entity.Favorite;
import com.bookverse.repository.BookGenreRepository;
import com.bookverse.repository.BookRepository;
import com.bookverse.repository.FavoriteRepository;
import com.bookverse.service.RecommendationService;
import com.bookverse.service.mapper.EntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final BookRepository bookRepository;
    private final FavoriteRepository favoriteRepository;
    private final BookGenreRepository bookGenreRepository;

    @Override
    public List<RecommendationDTO> getTopRated(int limit) {
        Page<Book> page = bookRepository.findTopRatedBooks(PageRequest.of(0, limit));
        List<BookDTO> books = page.getContent().stream()
                .map(EntityMapper::toBookDTO)
                .collect(Collectors.toList());
        
        return List.of(RecommendationDTO.builder()
                .type("top-rated")
                .title("Top Rated Books")
                .description("Books with the highest ratings from our community")
                .books(books)
                .build());
    }

    @Override
    public List<RecommendationDTO> getUserBasedRecommendations(Long userId, int limit) {
        List<RecommendationDTO> recommendations = new ArrayList<>();
        
        // Get user's favorite books
        List<Favorite> userFavorites = favoriteRepository.findByUserIdWithBook(userId);
        
        if (userFavorites.isEmpty()) {
            // If user has no favorites, return popular books in popular genres
            List<BookDTO> popularBooks = getPopularBooksInPopularGenres(limit);
            return List.of(RecommendationDTO.builder()
                    .type("popular")
                    .title("Popular Books")
                    .description("Trending books in our community")
                    .books(popularBooks)
                    .build());
        }
        
        // Get genres from user's favorite books
        Set<BookGenre.Genre> userFavoriteGenres = userFavorites.stream()
                .flatMap(favorite -> {
                    List<BookGenre> genres = favorite.getBook().getGenres();
                    return genres != null ? genres.stream() : Stream.empty();
                })
                .map(BookGenre::getGenre)
                .collect(Collectors.toSet());
        
        if (!userFavoriteGenres.isEmpty()) {
            // Get books in user's favorite genres (excluding already favorited books)
            Set<Long> userFavoriteBookIds = userFavorites.stream()
                    .map(favorite -> favorite.getBook().getId())
                    .collect(Collectors.toSet());
            
            Page<Book> genreBooks = bookRepository.findBooks(
                    null, null, new ArrayList<>(userFavoriteGenres), null, null, null, 
                    PageRequest.of(0, limit * 2)
            );
            
            List<BookDTO> recommendedBooks = genreBooks.getContent().stream()
                    .filter(book -> !userFavoriteBookIds.contains(book.getId()))
                    .limit(limit)
                    .map(EntityMapper::toBookDTO)
                    .collect(Collectors.toList());
            
            if (!recommendedBooks.isEmpty()) {
                recommendations.add(RecommendationDTO.builder()
                        .type("user-genre-based")
                        .title("Because you like these genres")
                        .description("Books in genres you've shown interest in")
                        .books(recommendedBooks)
                        .build());
            }
        }
        
        // If we don't have enough genre-based recommendations, add popular books
        if (recommendations.isEmpty() || recommendations.get(0).getBooks().size() < limit) {
            List<BookDTO> popularBooks = getPopularBooksInPopularGenres(limit);
            if (!popularBooks.isEmpty()) {
                recommendations.add(RecommendationDTO.builder()
                        .type("popular")
                        .title("Popular Books")
                        .description("Trending books in our community")
                        .books(popularBooks)
                        .build());
            }
        }
        
        return recommendations;
    }

    @Override
    public List<RecommendationDTO> getGenreBased(int limit) {
        // Get books from popular genres (Romance, Mystery, Fantasy, etc.)
        List<BookGenre.Genre> popularGenres = List.of(
            BookGenre.Genre.ROMANCE,
            BookGenre.Genre.MYSTERY,
            BookGenre.Genre.FANTASY,
            BookGenre.Genre.SCI_FI
        );
        
        Page<Book> genreBooks = bookRepository.findBooks(
            null, null, popularGenres, null, null, null, PageRequest.of(0, limit)
        );
        List<BookDTO> books = genreBooks.getContent().stream()
                .map(EntityMapper::toBookDTO)
                .collect(Collectors.toList());
        
        return List.of(RecommendationDTO.builder()
                .type("genre-based")
                .title("Popular Genres")
                .description("Books from popular genres like Fiction, Mystery, Romance, and Science Fiction")
                .books(books)
                .build());
    }
    
    @Override
    public List<RecommendationDTO> getGenreBasedFromFavorites(Long userId, int limit) {
        // Get user's favorite books
        List<Favorite> userFavorites = favoriteRepository.findByUserIdWithBook(userId);
        
        System.out.println("DEBUG: User " + userId + " has " + userFavorites.size() + " favorites");
        
        if (userFavorites.isEmpty()) {
            // Fallback to popular books if no favorites
            List<BookDTO> popularBooks = getPopularBooksInPopularGenres(limit);
            return List.of(RecommendationDTO.builder()
                    .type("popular")
                    .title("Popular Books")
                    .description("Trending books in our community")
                    .books(popularBooks)
                    .build());
        }
        
        // Debug: Print favorite books and their genres
        for (Favorite favorite : userFavorites) {
            Book book = favorite.getBook();
            System.out.println("DEBUG: Favorite book: " + book.getTitle() + " (ID: " + book.getId() + ")");
            if (book.getGenres() != null && !book.getGenres().isEmpty()) {
                System.out.println("DEBUG: Genres: " + book.getGenres().stream()
                        .map(bg -> bg.getGenre().name())
                        .collect(Collectors.joining(", ")));
            } else {
                System.out.println("DEBUG: No genres found for this book");
            }
        }
        
        // Calculate genre weights with frequency and recency
        Map<BookGenre.Genre, Double> genreWeights = calculateGenreWeights(userFavorites);
        
        System.out.println("DEBUG: Genre weights: " + genreWeights);
        
        // Get books from top-weighted genres
        Set<Long> userFavoriteBookIds = userFavorites.stream()
                .map(favorite -> favorite.getBook().getId())
                .collect(Collectors.toSet());
        
        List<BookDTO> recommendedBooks = getBooksFromTopGenres(genreWeights, userFavoriteBookIds, limit);
        
        System.out.println("DEBUG: Found " + recommendedBooks.size() + " recommended books");
        
        return List.of(RecommendationDTO.builder()
                .type("favorites-genre-based")
                .title("Based on your favorite genres")
                .description("Books in genres you love")
                .books(recommendedBooks)
                .build());
    }
    
    private Map<BookGenre.Genre, Double> calculateGenreWeights(List<Favorite> userFavorites) {
        Map<BookGenre.Genre, Double> genreWeights = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (Favorite favorite : userFavorites) {
            Book book = favorite.getBook();
            if (book.getGenres() != null) {
                for (BookGenre bookGenre : book.getGenres()) {
                    BookGenre.Genre genre = bookGenre.getGenre();
                    
                    // Base weight from frequency
                    double weight = genreWeights.getOrDefault(genre, 0.0) + 1.0;
                    
                    // Add recency bonus (recent favorites get higher weight)
                    long daysSinceFavorite = java.time.Duration.between(favorite.getCreatedAt(), now).toDays();
                    double recencyBonus = Math.max(0.1, 1.0 - (daysSinceFavorite / 30.0)); // Decay over 30 days
                    
                    // Add rating bonus
                    double ratingBonus = 0.0;
                    Double avgRating = book.getAverageRating();
                    if (avgRating != null) {
                        ratingBonus = (avgRating - 3.0) / 2.0;
                        ratingBonus = Math.max(0.0, Math.min(1.0, ratingBonus)); // Clamp between 0 and 1
                    }
                    
                    weight += recencyBonus + ratingBonus;
                    genreWeights.put(genre, weight);
                }
            }
        }
        
        return genreWeights;
    }
    
    private List<BookDTO> getBooksFromTopGenres(Map<BookGenre.Genre, Double> genreWeights, Set<Long> excludeBookIds, int limit) {
        // Sort genres by weight (descending)
        List<BookGenre.Genre> topGenres = genreWeights.entrySet().stream()
                .sorted(Map.Entry.<BookGenre.Genre, Double>comparingByValue().reversed())
                .limit(3) // Top 3 genres
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        
        System.out.println("DEBUG: Top genres: " + topGenres.stream().map(Enum::name).collect(Collectors.joining(", ")));
        
        if (topGenres.isEmpty()) {
            System.out.println("DEBUG: No top genres found, falling back to popular books");
            return getPopularBooksInPopularGenres(limit);
        }
        
        // Get books from top genres (no rating filter, will sort by rating later)
        Page<Book> genreBooks = bookRepository.findBooks(
                null, null, topGenres, null, null, null, PageRequest.of(0, limit * 2)
        );
        
        System.out.println("DEBUG: Found " + genreBooks.getTotalElements() + " books in top genres");
        
        // Filter and sort by genre weight and rating
        List<BookDTO> result = genreBooks.getContent().stream()
                .filter(book -> !excludeBookIds.contains(book.getId()))
                .sorted((b1, b2) -> {
                    // Sort by genre weight first, then by rating
                    double weight1 = getBookGenreWeight(b1, genreWeights);
                    double weight2 = getBookGenreWeight(b2, genreWeights);
                    
                    if (Double.compare(weight1, weight2) != 0) {
                        return Double.compare(weight2, weight1); // Descending
                    }
                    
                    // If same weight, sort by rating
                    Double avgRating1 = b1.getAverageRating();
                    Double avgRating2 = b2.getAverageRating();
                    double rating1 = avgRating1 != null ? avgRating1 : 0.0;
                    double rating2 = avgRating2 != null ? avgRating2 : 0.0;
                    return Double.compare(rating2, rating1); // Descending
                })
                .limit(limit)
                .map(EntityMapper::toBookDTO)
                .collect(Collectors.toList());
        
        System.out.println("DEBUG: After filtering and sorting: " + result.size() + " books");
        return result;
    }
    
    private double getBookGenreWeight(Book book, Map<BookGenre.Genre, Double> genreWeights) {
        if (book.getGenres() == null || book.getGenres().isEmpty()) {
            return 0.0;
        }
        
        // Return the highest weight among the book's genres
        return book.getGenres().stream()
                .mapToDouble(bg -> genreWeights.getOrDefault(bg.getGenre(), 0.0))
                .max()
                .orElse(0.0);
    }
    
    private List<BookDTO> getPopularBooksInPopularGenres(int limit) {
        // Get most favorited books as a fallback
        Page<Object[]> mostFavorited = favoriteRepository.findMostFavoritedBooks(PageRequest.of(0, limit));
        List<Long> bookIds = mostFavorited.getContent().stream()
                .map(row -> (Long) row[0])
                .collect(Collectors.toList());
        
        if (bookIds.isEmpty()) {
            // If no favorites exist, return most reviewed books
            Page<Book> mostReviewed = bookRepository.findBooks(
                null, null, null, null, null, null, PageRequest.of(0, limit)
            );
            return mostReviewed.getContent().stream()
                    .map(EntityMapper::toBookDTO)
                    .collect(Collectors.toList());
        }
        
        // Get book details for most favorited books
        List<Book> books = bookRepository.findAllById(bookIds);
        return books.stream()
                .map(EntityMapper::toBookDTO)
                .collect(Collectors.toList());
    }
}
