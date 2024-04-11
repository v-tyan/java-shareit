package ru.practicum.shareit.user;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Requested list of All Users");
        return UserMapper.mapToUserDto(userRepository.findAll());
    }

    @Override
    public UserDto getUser(long id) {
        log.info("Requested User id = {}", id);
        return userRepository.findById(id)
                .map(UserMapper::toUserDto)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        log.info("Request to create User = {}", userDto);
        User user = UserMapper.fromUserDto(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDto updateUser(long id, UserDto userDto) {
        log.info("Request to update User = {} with id = {}", userDto, id);
        return userRepository.findById(id)
                .map(u -> {
                    if (userDto.getEmail() != null)
                        u.setEmail(userDto.getEmail());
                    if (userDto.getName() != null)
                        u.setName(userDto.getName());
                    return UserMapper.toUserDto(userRepository.save(u));
                })
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

}
