package com.bookverse.controller;

import com.bookverse.dto.*;
import com.bookverse.security.JwtTokenProvider;
import com.bookverse.service.TokenBlacklistService;
import com.bookverse.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider tokenProvider;
    private final TokenBlacklistService tokenBlacklistService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<AuthResponse>> signup(@Valid @RequestBody UserRegistrationDTO request) {
        UserDTO user = userService.register(request);
        String token = tokenProvider.generateToken(user.getEmail());
        return ResponseEntity.ok(ApiResponse.ok(AuthResponse.builder().token(token).user(user).build()));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody UserLoginDTO request) {
        return userService.login(request)
                .map(user -> {
                    String token = tokenProvider.generateToken(user.getEmail());
                    return ResponseEntity.ok(ApiResponse.ok(AuthResponse.builder().token(token).user(user).build()));
                })
                .orElse(ResponseEntity.status(401).body(ApiResponse.error("Invalid credentials")));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request) {
        // Get the token from the Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenBlacklistService.blacklistToken(token);
        }
        return ResponseEntity.ok(ApiResponse.ok("Logged out successfully"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getName())) {
            return userService.getCurrentUserProfile()
                    .map(user -> ResponseEntity.ok(ApiResponse.ok(user)))
                    .orElse(ResponseEntity.status(404).body(ApiResponse.error("User not found")));
        }
        return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
    }
}
