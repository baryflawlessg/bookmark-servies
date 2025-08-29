package com.bookverse.controller;

import com.bookverse.dto.*;
import com.bookverse.security.JwtTokenProvider;
import com.bookverse.service.TokenBlacklistService;
import com.bookverse.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Authentication", description = "User authentication and authorization endpoints")
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider tokenProvider;
    private final TokenBlacklistService tokenBlacklistService;

    @PostMapping("/signup")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "User Registration",
        description = "Register a new user account with name, email, and password"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User registered successfully",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.bookverse.dto.ApiResponse.class),
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    name = "Success Response",
                    value = """
                        {
                          "success": true,
                          "message": "OK",
                          "data": {
                            "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                            "user": {
                              "id": 1,
                              "name": "John Doe",
                              "email": "john@example.com"
                            }
                          }
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Validation error or email already exists",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.bookverse.dto.ApiResponse.class),
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    name = "Validation Error",
                    value = """
                        {
                          "success": false,
                          "message": "Email already in use"
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<com.bookverse.dto.ApiResponse<AuthResponse>> signup(
            @io.swagger.v3.oas.annotations.Parameter(
                description = "User registration details",
                required = true,
                content = @io.swagger.v3.oas.annotations.media.Content(
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "Registration Request",
                        value = """
                            {
                              "name": "John Doe",
                              "email": "john@example.com",
                              "password": "securePassword123"
                            }
                            """
                    )
                )
            )
            @Valid @RequestBody UserRegistrationDTO request) {
        UserDTO user = userService.register(request);
        String token = tokenProvider.generateToken(user.getEmail());
        return ResponseEntity.ok(com.bookverse.dto.ApiResponse.ok(AuthResponse.builder().token(token).user(user).build()));
    }

    @PostMapping("/login")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "User Login",
        description = "Authenticate user with email and password, return JWT token"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.bookverse.dto.ApiResponse.class),
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    name = "Success Response",
                    value = """
                        {
                          "success": true,
                          "message": "OK",
                          "data": {
                            "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                            "user": {
                              "id": 1,
                              "name": "John Doe",
                              "email": "john@example.com"
                            }
                          }
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Invalid credentials",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.bookverse.dto.ApiResponse.class),
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    name = "Invalid Credentials",
                    value = """
                        {
                          "success": false,
                          "message": "Invalid credentials"
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<com.bookverse.dto.ApiResponse<AuthResponse>> login(
            @io.swagger.v3.oas.annotations.Parameter(
                description = "User login credentials",
                required = true,
                content = @io.swagger.v3.oas.annotations.media.Content(
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "Login Request",
                        value = """
                            {
                              "email": "john@example.com",
                              "password": "securePassword123"
                            }
                            """
                    )
                )
            )
            @Valid @RequestBody UserLoginDTO request) {
        return userService.login(request)
                .map(user -> {
                    String token = tokenProvider.generateToken(user.getEmail());
                    return ResponseEntity.ok(com.bookverse.dto.ApiResponse.ok(AuthResponse.builder().token(token).user(user).build()));
                })
                .orElse(ResponseEntity.status(401).body(com.bookverse.dto.ApiResponse.error("Invalid credentials")));
    }

    @PostMapping("/logout")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "User Logout",
        description = "Invalidate the current JWT token (add to blacklist)"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Logout successful",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.bookverse.dto.ApiResponse.class),
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    name = "Success Response",
                    value = """
                        {
                          "success": true,
                          "message": "Logged out successfully"
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<com.bookverse.dto.ApiResponse<String>> logout(
            @io.swagger.v3.oas.annotations.Parameter(description = "HTTP request containing Authorization header") 
            HttpServletRequest request) {
        // Get the token from the Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenBlacklistService.blacklistToken(token);
        }
        return ResponseEntity.ok(com.bookverse.dto.ApiResponse.ok("Logged out successfully"));
    }

    @GetMapping("/me")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Get Current User Profile",
        description = "Retrieve the profile of the currently authenticated user"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User profile retrieved successfully",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.bookverse.dto.ApiResponse.class),
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    name = "Success Response",
                    value = """
                        {
                          "success": true,
                          "message": "OK",
                          "data": {
                            "id": 1,
                            "name": "John Doe",
                            "email": "john@example.com"
                          }
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Not authenticated",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.bookverse.dto.ApiResponse.class),
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    name = "Not Authenticated",
                    value = """
                        {
                          "success": false,
                          "message": "Not authenticated"
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.bookverse.dto.ApiResponse.class),
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    name = "User Not Found",
                    value = """
                        {
                          "success": false,
                          "message": "User not found"
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<com.bookverse.dto.ApiResponse<UserDTO>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getName())) {
            return userService.getCurrentUserProfile()
                    .map(user -> ResponseEntity.ok(com.bookverse.dto.ApiResponse.ok(user)))
                    .orElse(ResponseEntity.status(404).body(com.bookverse.dto.ApiResponse.error("User not found")));
        }
        return ResponseEntity.status(401).body(com.bookverse.dto.ApiResponse.error("Not authenticated"));
    }
}
