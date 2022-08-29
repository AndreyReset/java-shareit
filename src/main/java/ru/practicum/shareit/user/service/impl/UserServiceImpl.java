package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findUserById(long id) {
        return Optional.ofNullable(userRepository.findById(id)
                .orElseThrow(() -> new ObjNotFoundException("Пользователь не найден")));
    }

    @Override
    @Transactional
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User update(User user, long id) {
        Optional<User> oldUser = findUserById(id);
        if (oldUser.isPresent()) {
            user.setId(id);
            if (user.getName() == null)
                user.setName(oldUser.get().getName());
            if (user.getEmail() == null)
                user.setEmail(oldUser.get().getEmail());
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void delete(long id) {
        userRepository.deleteById(id);
    }
}
