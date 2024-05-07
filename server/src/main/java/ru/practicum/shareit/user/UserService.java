package ru.practicum.shareit.user;

import java.util.List;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    public List<UserDto> getAllUsers();

    public UserDto getUser(long id);

    public UserDto createUser(UserDto userDto);

    public UserDto updateUser(long id, UserDto userDto);

    public void deleteUser(long id);
}
