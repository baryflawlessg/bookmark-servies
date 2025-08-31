package com.bookverse.controller;

import com.bookverse.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HealthControllerTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private HealthController healthController;

    @Test
    void health_ShouldReturnOkResponse() {
        ResponseEntity<String> response = healthController.health();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("OK", response.getBody());
    }

    @Test
    void databaseHealth_WithSuccess_ShouldReturnOkResponse() {
        when(bookRepository.count()).thenReturn(42L);
        ResponseEntity<String> response = healthController.databaseHealth();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Database OK"));
        verify(bookRepository).count();
    }

    @Test
    void databaseHealth_WithException_ShouldReturnErrorResponse() {
        when(bookRepository.count()).thenThrow(new RuntimeException("DB Error"));
        ResponseEntity<String> response = healthController.databaseHealth();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Database Error"));
        verify(bookRepository).count();
    }
}
