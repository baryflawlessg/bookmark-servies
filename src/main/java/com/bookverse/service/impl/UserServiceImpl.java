package com.bookverse.service.impl;

import com.bookverse.dto.UserDTO;
import com.bookverse.dto.UserLoginDTO;
import com.bookverse.dto.UserRegistrationDTO;
import com.bookverse.entity.User;
import com.bookverse.repository.UserRepository;
import com.bookverse.service.UserService;
import com.bookverse.service.mapper.EntityMapper;
import jakarta.transaction.Transactional;
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
    public Optional<UserDTO> login(UserLoginDTO request) {
        return userRepository.findByEmail(request.getEmail())
                .filter(u -> passwordEncoder.matches(request.getPassword(), u.getPassword()))
                .map(EntityMapper::toUserDTO);
    }

    @Override
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
    public Optional<UserDTO> getUserById(Long userId) {
        return userRepository.findById(userId).map(EntityMapper::toUserDTO);
    }
}
