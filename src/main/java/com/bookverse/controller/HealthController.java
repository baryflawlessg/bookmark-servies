package com.bookverse.controller;

import com.bookverse.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
public class HealthController {

    private final BookRepository bookRepository;

    @GetMapping
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/db")
    public ResponseEntity<String> databaseHealth() {
        try {
            long count = bookRepository.count();
            return ResponseEntity.ok("Database OK - Book count: " + count);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Database Error: " + e.getMessage());
        }
    }
}
