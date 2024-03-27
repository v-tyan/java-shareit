package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    public List<User> getAllUsers();

    public User getUser(long id);

    public User createUser(User user);

    public User updateUser(long id, User user);

    public void deleteUser(long id);
}
