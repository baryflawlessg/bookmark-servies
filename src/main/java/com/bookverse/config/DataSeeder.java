package com.bookverse.config;

import com.bookverse.entity.Book;
import com.bookverse.entity.BookGenre;
import com.bookverse.entity.User;
import com.bookverse.repository.BookRepository;
import com.bookverse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    @Profile("!prod") // Only run in non-production environments
    public CommandLineRunner seedData() {
        return args -> {
            log.info("Starting data seeding...");
            
            // Seed books if none exist
            if (bookRepository.count() == 0) {
                seedBooks();
            }
            
            // Seed users if none exist
            if (userRepository.count() == 0) {
                seedUsers();
            }
            
            log.info("Data seeding completed!");
        };
    }

    private void seedBooks() {
        log.info("Seeding books...");
        
        List<Book> books = Arrays.asList(
            createBook("The Great Gatsby", "F. Scott Fitzgerald", 
                "A story of the fabulously wealthy Jay Gatsby and his love for the beautiful Daisy Buchanan.", 
                1925, "https://example.com/gatsby.jpg", 
                BookGenre.Genre.FICTION, BookGenre.Genre.ROMANCE),
            
            createBook("To Kill a Mockingbird", "Harper Lee", 
                "The story of young Scout Finch and her father Atticus in a racially divided Alabama town.", 
                1960, "https://example.com/mockingbird.jpg", 
                BookGenre.Genre.FICTION, BookGenre.Genre.DRAMA),
            
            createBook("1984", "George Orwell", 
                "A dystopian novel about totalitarianism and surveillance society.", 
                1949, "https://example.com/1984.jpg", 
                BookGenre.Genre.SCI_FI, BookGenre.Genre.FICTION),
            
            createBook("Pride and Prejudice", "Jane Austen", 
                "The story of Elizabeth Bennet and Mr. Darcy in early 19th century England.", 
                1813, "https://example.com/pride.jpg", 
                BookGenre.Genre.ROMANCE, BookGenre.Genre.FICTION),
            
            createBook("The Hobbit", "J.R.R. Tolkien", 
                "Bilbo Baggins embarks on an unexpected journey with a group of dwarves.", 
                1937, "https://example.com/hobbit.jpg", 
                BookGenre.Genre.FANTASY, BookGenre.Genre.ADVENTURE),
            
            createBook("The Catcher in the Rye", "J.D. Salinger", 
                "Holden Caulfield's journey through New York City after being expelled from prep school.", 
                1951, "https://example.com/catcher.jpg", 
                BookGenre.Genre.FICTION, BookGenre.Genre.YOUNG_ADULT),
            
            createBook("Lord of the Flies", "William Golding", 
                "A group of British boys stranded on an uninhabited island try to govern themselves.", 
                1954, "https://example.com/flies.jpg", 
                BookGenre.Genre.FICTION, BookGenre.Genre.ADVENTURE),
            
            createBook("Animal Farm", "George Orwell", 
                "A farm is taken over by its overworked, mistreated animals.", 
                1945, "https://example.com/animal-farm.jpg", 
                BookGenre.Genre.FICTION, BookGenre.Genre.HUMOR),
            
            createBook("The Alchemist", "Paulo Coelho", 
                "A shepherd boy named Santiago travels from his homeland in Spain to the Egyptian desert.", 
                1988, "https://example.com/alchemist.jpg", 
                BookGenre.Genre.FICTION, BookGenre.Genre.ADVENTURE),
            
            createBook("Brave New World", "Aldous Huxley", 
                "A dystopian novel about a futuristic society controlled by technology and conditioning.", 
                1932, "https://example.com/brave-new-world.jpg", 
                BookGenre.Genre.SCI_FI, BookGenre.Genre.FICTION)
        );
        
        bookRepository.saveAll(books);
        log.info("Seeded {} books", books.size());
    }

    private Book createBook(String title, String author, String description, 
                           int publishedYear, String coverImageUrl, 
                           BookGenre.Genre... genres) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setDescription(description);
        book.setPublishedYear(publishedYear);
        book.setCoverImageUrl(coverImageUrl);
        
        // Initialize rating stats
        book.setAverageRating(0.0);
        book.setReviewCount(0);
        
        // Add genres
        for (BookGenre.Genre genre : genres) {
            BookGenre bookGenre = new BookGenre();
            bookGenre.setGenre(genre);
            book.addGenre(bookGenre);
        }
        
        return book;
    }

    private void seedUsers() {
        log.info("Seeding users...");
        
        List<User> users = Arrays.asList(
            createUser("john.doe@example.com", "password123", "John Doe"),
            createUser("jane.smith@example.com", "password123", "Jane Smith"),
            createUser("admin@bookverse.com", "admin123", "Admin User"),
            createUser("1@email.com", "email1", "User One")
        );
        
        userRepository.saveAll(users);
        log.info("Seeded {} users", users.size());
    }

    private User createUser(String email, String password, String name) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setName(name);
        return user;
    }
}
