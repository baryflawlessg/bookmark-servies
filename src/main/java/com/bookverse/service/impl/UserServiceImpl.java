package com.bookverse.service.impl;

import com.bookverse.dto.UserDTO;
import com.bookverse.dto.UserLoginDTO;
import com.bookverse.dto.UserRegistrationDTO;
import com.bookverse.dto.UserUpdateDTO;
import com.bookverse.entity.User;
import com.bookverse.repository.UserRepository;
import com.bookverse.service.UserService;
import com.bookverse.service.mapper.EntityMapper;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDTO register(UserRegistrationDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        User saved = userRepository.save(user);
        return EntityMapper.toUserDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> login(UserLoginDTO request) {
        return userRepository.findByEmail(request.getEmail())
                .filter(u -> passwordEncoder.matches(request.getPassword(), u.getPassword()))
                .map(EntityMapper::toUserDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getName())) {
            return userRepository.findByEmail(authentication.getName())
                    .map(EntityMapper::toUserDTO);
        }
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserById(Long userId) {
        return userRepository.findById(userId).map(EntityMapper::toUserDTO);
    }

    @Override
    @Transactional
    public Optional<UserDTO> updateProfile(String email, UserUpdateDTO request) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    if (request.getName() != null) {
                        user.setName(request.getName());
                    }
                    if (request.getEmail() != null && !request.getEmail().equals(email)) {
                        // Check if new email is already in use
                        if (userRepository.existsByEmail(request.getEmail())) {
                            throw new IllegalArgumentException("Email already in use");
                        }
                        user.setEmail(request.getEmail());
                    }
                    if (request.getPassword() != null) {
                        user.setPassword(passwordEncoder.encode(request.getPassword()));
                    }
                    User saved = userRepository.save(user);
                    return EntityMapper.toUserDTO(saved);
                });
    }

    @Override
    @Transactional
    public boolean deleteAccount(String email) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    userRepository.delete(user);
                    return true;
                })
                .orElse(false);
    }
}
