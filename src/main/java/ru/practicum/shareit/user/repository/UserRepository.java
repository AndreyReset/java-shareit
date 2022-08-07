package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<User> findAll();

    Optional<User> findUserById(long id);

    User create(User user);

    User update(User user, long id);

    void delete(long id);
}