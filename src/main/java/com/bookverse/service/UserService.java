package com.bookverse.service;

import com.bookverse.dto.UserDTO;
import com.bookverse.dto.UserLoginDTO;
import com.bookverse.dto.UserRegistrationDTO;

import java.util.Optional;

public interface UserService {
    UserDTO register(UserRegistrationDTO request);
    Optional<UserDTO> login(UserLoginDTO request);
    Optional<UserDTO> getCurrentUserProfile();
    Optional<UserDTO> getUserById(Long userId);
}
