package ru.practicum.shareit.user.repository.impl;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ObjNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class InMemoryUserRepositoryImpl implements UserRepository {

    private final List<User> users = new ArrayList<>();
    private long idUser = 1;

    @Override
    public List<User> findAll() {
        return users;
    }

    @Override
    public Optional<User> findUserById(long id) {
        return users.stream()
                .filter(u -> u.getId() == id)
                .findFirst();
    }

    @Override
    public User create(User user) {
        if (users.stream().anyMatch(u -> Objects.equals(u.getEmail(), user.getEmail())))
            throw new ConflictException("Пользователь с e-mail `" + user.getEmail() + "` уже зарегистрирован");
        user.setId(idUser);
        idUser++;
        users.add(user);
        return user;
    }

    @Override
    public User update(User user, long id) {
        if (users.stream().anyMatch(u -> Objects.equals(u.getEmail(), user.getEmail())))
            throw new ConflictException("Пользователь с e-mail `" + user.getEmail() + "` уже зарегистрирован");
        for (User u : users) {
            if (u.getId() == id) {
                if (user.getEmail() != null)
                    u.setEmail(user.getEmail());
                if (user.getName() != null)
                    u.setName(user.getName());
                return u;
            }
        }
        throw new ObjNotFoundException("Пользователь с e-mail `" + user.getEmail() + "` не найден");
    }

    @Override
    public void delete(long id) {
        for (User u : users) {
            if (u.getId() == id) {
                users.remove(u);
                break;
            }
        }
    }
}
