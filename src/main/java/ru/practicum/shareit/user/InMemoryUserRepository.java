package ru.practicum.shareit.user;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import ru.practicum.shareit.user.exception.UserAlreadyExistsException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

@Primary
@Component
public class InMemoryUserRepository implements UserRepository {
    private long id = 1;
    private HashMap<Long, User> users = new HashMap<>();

    private long getNextId() {
        return id++;
    }

    @Override
    public List<User> getAllUsers() {
        return List.copyOf(users.values());
    }

    @Override
    public User getUser(long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new UserNotFoundException(String.format("User with id = %d not found", id));
        }
    }

    @Override
    public User createUser(User user) {
        if (!checkIfExists(user)) {
            user.setId(getNextId());
            users.put(user.getId(), user);
            return user;
        } else {
            throw new UserAlreadyExistsException(String.format("User with email = %s not found", user.getEmail()));
        }
    }

    @Override
    public User updateUser(long id, User user) {
        if (users.containsKey(id)) {
            if (users.get(id).getEmail().equals(user.getEmail())) {
                if (user.getName() != null) {
                    users.get(id).setName(user.getName());
                }
            } else {
                if (!checkIfExists(user)) {
                    if (user.getName() != null)
                        users.get(id).setName(user.getName());
                    if (user.getEmail() != null)
                        users.get(id).setEmail(user.getEmail());
                } else {
                    throw new UserAlreadyExistsException(
                            String.format("User with email = %s not found", user.getEmail()));
                }
            }
            return users.get(id);
        } else {
            throw new UserNotFoundException(String.format("User with id = %d not found", id));
        }
    }

    private boolean checkIfExists(User user) {
        if (user.getEmail() == null)
            return false;
        List<String> existingEmails = users.values().stream().map(u -> u.getEmail()).collect(Collectors.toList());
        for (String email : existingEmails) {
            if (user.getEmail().equals(email))
                return true;
        }
        return false;
    }

    @Override
    public void deleteUser(long id) {
        users.remove(id);
    }
}
