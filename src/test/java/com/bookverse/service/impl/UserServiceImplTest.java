package com.bookverse.service.impl;

import com.bookverse.dto.UserDTO;
import com.bookverse.dto.UserLoginDTO;
import com.bookverse.dto.UserRegistrationDTO;
import com.bookverse.dto.UserUpdateDTO;
import com.bookverse.entity.User;
import com.bookverse.repository.UserRepository;
import com.bookverse.service.mapper.EntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserRegistrationDTO registrationRequest;
    private UserLoginDTO loginRequest;
    private UserUpdateDTO updateRequest;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");

        // Setup registration request
        registrationRequest = UserRegistrationDTO.builder()
            .name("New User")
            .email("newuser@example.com")
            .password("password123")
            .build();

        // Setup login request
        loginRequest = UserLoginDTO.builder()
            .email("test@example.com")
            .password("password123")
            .build();

        // Setup update request
        updateRequest = UserUpdateDTO.builder()
            .name("Updated User")
            .email("updated@example.com")
            .password("newpassword123")
            .build();
    }

    @Test
    void register_WithValidData_ShouldReturnUserDTO() {
        // Arrange
        when(userRepository.existsByEmail(registrationRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registrationRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        UserDTO result = userService.register(registrationRequest);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getName(), result.getName());
        assertEquals(testUser.getEmail(), result.getEmail());

        // Verify repository calls
        verify(userRepository, times(1)).existsByEmail(registrationRequest.getEmail());
        verify(passwordEncoder, times(1)).encode(registrationRequest.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository, passwordEncoder);
    }

    @Test
    void register_WhenEmailAlreadyExists_ShouldThrowIllegalArgumentException() {
        // Arrange
        when(userRepository.existsByEmail(registrationRequest.getEmail())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.register(registrationRequest)
        );

        assertEquals("Email already in use", exception.getMessage());

        // Verify repository calls
        verify(userRepository, times(1)).existsByEmail(registrationRequest.getEmail());
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void register_ShouldEncodePassword() {
        // Arrange
        when(userRepository.existsByEmail(registrationRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registrationRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.register(registrationRequest);

        // Verify password was encoded
        verify(passwordEncoder, times(1)).encode(registrationRequest.getPassword());
    }

    @Test
    void login_WithValidCredentials_ShouldReturnUserDTO() {
        // Arrange
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), testUser.getPassword())).thenReturn(true);

        // Act
        Optional<UserDTO> result = userService.login(loginRequest);

        // Assert
        assertTrue(result.isPresent());
        UserDTO userDTO = result.get();
        assertEquals(testUser.getId(), userDTO.getId());
        assertEquals(testUser.getName(), userDTO.getName());
        assertEquals(testUser.getEmail(), userDTO.getEmail());

        // Verify repository calls
        verify(userRepository, times(1)).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder, times(1)).matches(loginRequest.getPassword(), testUser.getPassword());
        verifyNoMoreInteractions(userRepository, passwordEncoder);
    }

    @Test
    void login_WhenUserNotFound_ShouldReturnEmpty() {
        // Arrange
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        // Act
        Optional<UserDTO> result = userService.login(loginRequest);

        // Assert
        assertFalse(result.isPresent());

        // Verify repository calls
        verify(userRepository, times(1)).findByEmail(loginRequest.getEmail());
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void login_WhenPasswordDoesNotMatch_ShouldReturnEmpty() {
        // Arrange
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), testUser.getPassword())).thenReturn(false);

        // Act
        Optional<UserDTO> result = userService.login(loginRequest);

        // Assert
        assertFalse(result.isPresent());

        // Verify repository calls
        verify(userRepository, times(1)).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder, times(1)).matches(loginRequest.getPassword(), testUser.getPassword());
        verifyNoMoreInteractions(userRepository, passwordEncoder);
    }

    @Test
    void getCurrentUserProfile_WhenUserIsAuthenticated_ShouldReturnUserDTO() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act
        Optional<UserDTO> result = userService.getCurrentUserProfile();

        // Assert
        assertTrue(result.isPresent());
        UserDTO userDTO = result.get();
        assertEquals(testUser.getId(), userDTO.getId());
        assertEquals(testUser.getName(), userDTO.getName());
        assertEquals(testUser.getEmail(), userDTO.getEmail());

        // Verify repository calls
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void getCurrentUserProfile_WhenUserIsNotAuthenticated_ShouldReturnEmpty() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(false);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Act
        Optional<UserDTO> result = userService.getCurrentUserProfile();

        // Assert
        assertFalse(result.isPresent());

        // Verify no repository calls
        verifyNoInteractions(userRepository, passwordEncoder);
    }

    @Test
    void getCurrentUserProfile_WhenAuthenticationIsNull_ShouldReturnEmpty() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        // Act
        Optional<UserDTO> result = userService.getCurrentUserProfile();

        // Assert
        assertFalse(result.isPresent());

        // Verify no repository calls
        verifyNoInteractions(userRepository, passwordEncoder);
    }

    @Test
    void getCurrentUserProfile_WhenUserIsAnonymous_ShouldReturnEmpty() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("anonymousUser");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Act
        Optional<UserDTO> result = userService.getCurrentUserProfile();

        // Assert
        assertFalse(result.isPresent());

        // Verify no repository calls
        verifyNoInteractions(userRepository, passwordEncoder);
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUserDTO() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        Optional<UserDTO> result = userService.getUserById(userId);

        // Assert
        assertTrue(result.isPresent());
        UserDTO userDTO = result.get();
        assertEquals(testUser.getId(), userDTO.getId());
        assertEquals(testUser.getName(), userDTO.getName());
        assertEquals(testUser.getEmail(), userDTO.getEmail());

        // Verify repository calls
        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        Optional<UserDTO> result = userService.getUserById(userId);

        // Assert
        assertFalse(result.isPresent());

        // Verify repository calls
        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void updateProfile_WithValidData_ShouldReturnUpdatedUserDTO() {
        // Arrange
        String currentEmail = "test@example.com";
        when(userRepository.findByEmail(currentEmail)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(updateRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(updateRequest.getPassword())).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        Optional<UserDTO> result = userService.updateProfile(currentEmail, updateRequest);

        // Assert
        assertTrue(result.isPresent());
        UserDTO userDTO = result.get();
        assertEquals(testUser.getId(), userDTO.getId());

        // Verify repository calls
        verify(userRepository, times(1)).findByEmail(currentEmail);
        verify(userRepository, times(1)).existsByEmail(updateRequest.getEmail());
        verify(passwordEncoder, times(1)).encode(updateRequest.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository, passwordEncoder);
    }

    @Test
    void updateProfile_WhenUserNotFound_ShouldReturnEmpty() {
        // Arrange
        String currentEmail = "nonexistent@example.com";
        when(userRepository.findByEmail(currentEmail)).thenReturn(Optional.empty());

        // Act
        Optional<UserDTO> result = userService.updateProfile(currentEmail, updateRequest);

        // Assert
        assertFalse(result.isPresent());

        // Verify repository calls
        verify(userRepository, times(1)).findByEmail(currentEmail);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void updateProfile_WhenNewEmailAlreadyExists_ShouldThrowIllegalArgumentException() {
        // Arrange
        String currentEmail = "test@example.com";
        when(userRepository.findByEmail(currentEmail)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(updateRequest.getEmail())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.updateProfile(currentEmail, updateRequest)
        );

        assertEquals("Email already in use", exception.getMessage());

        // Verify repository calls
        verify(userRepository, times(1)).findByEmail(currentEmail);
        verify(userRepository, times(1)).existsByEmail(updateRequest.getEmail());
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void updateProfile_WhenEmailIsSame_ShouldNotCheckEmailUniqueness() {
        // Arrange
        String currentEmail = "test@example.com";
        UserUpdateDTO sameEmailRequest = UserUpdateDTO.builder()
            .name("Updated User")
            .email(currentEmail) // Same email
            .password("newpassword123")
            .build();

        when(userRepository.findByEmail(currentEmail)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(sameEmailRequest.getPassword())).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        Optional<UserDTO> result = userService.updateProfile(currentEmail, sameEmailRequest);

        // Assert
        assertTrue(result.isPresent());

        // Verify repository calls - should not check email uniqueness for same email
        verify(userRepository, times(1)).findByEmail(currentEmail);
        verify(userRepository, never()).existsByEmail(currentEmail);
        verify(passwordEncoder, times(1)).encode(sameEmailRequest.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository, passwordEncoder);
    }

    @Test
    void updateProfile_WithPartialData_ShouldUpdateOnlyProvidedFields() {
        // Arrange
        String currentEmail = "test@example.com";
        UserUpdateDTO partialRequest = UserUpdateDTO.builder()
            .name("Updated Name")
            .build(); // Only name, no email or password

        when(userRepository.findByEmail(currentEmail)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        Optional<UserDTO> result = userService.updateProfile(currentEmail, partialRequest);

        // Assert
        assertTrue(result.isPresent());

        // Verify repository calls - should not check email or encode password
        verify(userRepository, times(1)).findByEmail(currentEmail);
        verify(userRepository, never()).existsByEmail(any());
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository, passwordEncoder);
    }

    @Test
    void deleteAccount_WhenUserExists_ShouldReturnTrue() {
        // Arrange
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        // Act
        boolean result = userService.deleteAccount(email);

        // Assert
        assertTrue(result);

        // Verify repository calls
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).delete(testUser);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void deleteAccount_WhenUserDoesNotExist_ShouldReturnFalse() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        boolean result = userService.deleteAccount(email);

        // Assert
        assertFalse(result);

        // Verify repository calls
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, never()).delete(any());
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void deleteAccount_ShouldDeleteUserFromRepository() {
        // Arrange
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        // Act
        userService.deleteAccount(email);

        // Verify user was deleted
        verify(userRepository, times(1)).delete(testUser);
    }
}
