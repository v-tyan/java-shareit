package ru.practicum.shareit.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserServiceImpl userService;

    User testUser;
    UserDto testUserDto;
    User updatedUser;
    UserDto updatedUserDto;

    @BeforeEach
    void beforeEach() {
        testUser = User.builder()
                .id(1L)
                .email("email@email.com")
                .name("name")
                .build();
        testUserDto = UserDto.builder()
                .id(1L)
                .email("email@email.com")
                .name("name")
                .build();
        updatedUser = User.builder()
                .id(1L)
                .email("update@update.com")
                .name("update_name")
                .build();
        updatedUserDto = UserDto.builder()
                .id(1L)
                .email("update@update.com")
                .name("update_name")
                .build();
    }

    @Test
    void getAll_ReturnEmptyList() {
        when(userRepository.findAll())
                .thenReturn(Collections.emptyList());
        assertTrue(userService.getAllUsers().isEmpty());
    }

    @Test
    void getById_Success() {
        when(userRepository.findById(0L))
                .thenReturn(Optional.of(testUser));
        var actualUser = userService.getUser(0L);
        assertEquals(testUserDto, actualUser);
    }

    @Test
    void getById_UserNotFound() {
        when(userRepository.findById(0L))
                .thenReturn(Optional.empty());
        var ex = assertThrows(UserNotFoundException.class,
                () -> userService.getUser(0L));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void create_With_Success() {
        when(userRepository.save(any())).thenReturn(testUser);
        assertEquals(testUserDto, userService.createUser(testUserDto));
        verify(userRepository).save(any());
    }

    @Test
    void update_NotFoundException() {
        when(userRepository.findById(0L))
                .thenReturn(Optional.empty());
        var ex = assertThrows(UserNotFoundException.class,
                () -> userService.updateUser(0L, updatedUserDto));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void update_with_only_new_email() {
        UserDto userUpdate = UserDto.builder()
                .email("update@update.com")
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testUser));
        when(userRepository.save(any()))
                .thenReturn(updatedUser);

        UserDto actual = userService.updateUser(1L, userUpdate);

        assertEquals(actual.getEmail(), userUpdate.getEmail());
    }

    @Test
    void update_with_only_new_name() {
        UserDto userUpdate = UserDto.builder()
                .name("update_name")
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testUser));
        when(userRepository.save(any()))
                .thenReturn(updatedUser);

        UserDto actual = userService.updateUser(1L, userUpdate);

        assertEquals(actual.getName(), userUpdate.getName());
    }

    @Test
    void delete_verifyInvokingMethod() {
        userService.deleteUser(1L);
        verify(userRepository).deleteById(any());
    }
}