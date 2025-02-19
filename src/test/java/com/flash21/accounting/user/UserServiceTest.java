package com.flash21.accounting.user;

import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.errorcode.UserErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static com.flash21.accounting.common.ErrorCodeAssertions.assertErrorCode;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserService userService;

    private UserRegisterDto userRegisterDto;
    private UserUpdateDto userUpdateDto;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userRegisterDto = new UserRegisterDto(
                "testuser", "password123", "John Doe",
                "010-1234-5678", "john@example.com",
                "Seoul", "Apt 101", Role.ROLE_ADMIN,
                "Grade A", "02-123-4567", "02-987-6543"
        );

        user = userRegisterDto.toEntity("hashedPassword");

        userUpdateDto = new UserUpdateDto(
                1L, "newPassword123", "John Smith",
                "010-8765-4321", "johnsmith@example.com",
                "Busan", "Apt 202", Role.ROLE_ADMIN,
                "Grade B", "02-765-4321", "02-123-4567"
        );
    }

    @Test
    @DisplayName("User registration - success")
    void register_Success() {
        when(userRepository.existsByUsername(userRegisterDto.username())).thenReturn(false);
        when(bCryptPasswordEncoder.encode(userRegisterDto.password())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserReadDto result = userService.register(userRegisterDto);

        assertNotNull(result);
        assertEquals(user.getName(), result.name());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("User registration - duplicate username")
    void register_DuplicateUsername_ThrowsException() {
        when(userRepository.existsByUsername(userRegisterDto.username())).thenReturn(true);

        assertErrorCode(UserErrorCode.DUPLICATE_USERNAME,
                () -> userService.register(userRegisterDto));
    }

    @Test
    @DisplayName("Find user by username - success")
    void findByUsername_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(user);

        UserReadDto result = userService.findByUsername("testuser");

        assertNotNull(result);
        assertEquals(user.getName(), result.name());
    }

    @Test
    @DisplayName("Find user by username - user not found")
    void findByUsername_UserNotFound_ThrowsException() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(null);

        assertErrorCode(UserErrorCode.USER_NOT_FOUND,
                () -> userService.findByUsername("nonexistent"));
    }

    @Test
    @DisplayName("Update user - success")
    void updateUser_Success() {
        when(userRepository.findById(userUpdateDto.id())).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.encode(userUpdateDto.password())).thenReturn("newHashedPassword");

        UserReadDto result = userService.updateUser(userUpdateDto);

        assertNotNull(result);
        verify(userRepository, times(1)).findById(eq(userUpdateDto.id()));
    }

    @Test
    @DisplayName("Update user - user not found")
    void updateUser_UserNotFound_ThrowsException() {
        when(userRepository.findById(userUpdateDto.id())).thenReturn(Optional.empty());

        assertErrorCode(UserErrorCode.USER_NOT_FOUND,
                () -> userService.updateUser(userUpdateDto));
    }

    @Test
    @DisplayName("Delete user - success")
    void deleteUser_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(bCryptPasswordEncoder.matches("password123", user.getPassword())).thenReturn(true);

        Long deletedUserId = userService.deleteUser("testuser", "password123");

        assertEquals(user.getId(), deletedUserId);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    @DisplayName("Delete user - user not found")
    void deleteUser_UserNotFound_ThrowsException() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(null);

        assertErrorCode(UserErrorCode.USER_NOT_FOUND,
                () -> userService.deleteUser("nonexistent", "password123"));
    }

    @Test
    @DisplayName("Delete user - password mismatch")
    void deleteUser_PasswordMismatch_ThrowsException() {
        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(bCryptPasswordEncoder.matches("wrongpassword", user.getPassword())).thenReturn(false);

        assertErrorCode(UserErrorCode.USERNAME_OR_PASSWORD_MISMATCH,
                () -> userService.deleteUser("testuser", "wrongpassword"));
    }
}
