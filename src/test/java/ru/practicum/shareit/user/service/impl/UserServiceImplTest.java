package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource("/application-test.properties")
@ActiveProfiles("test")
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    private final UserServiceImpl userService;

    private final EntityManager em;

    @Test
    public void findAll_Empty() {
        UserService userService = new UserServiceImpl(userRepository);
        Mockito.when(userRepository.findAll()).thenReturn(new ArrayList<>());
        List<User> users = userService.findAll();
        Assertions.assertEquals(users.size(), 0);
    }

    @Test
    public void findAll_NotEmpty() {
        UserService userService = new UserServiceImpl(userRepository);
        List<User> usersList = List.of(new User(), new User());
        Mockito.when(userRepository.findAll()).thenReturn(usersList);
        List<User> users = userService.findAll();
        Assertions.assertEquals(users.size(), 2);
    }

    @Test
    public void whenFindAll_thenCallRepository() {
        UserService userService = new UserServiceImpl(userRepository);
        Mockito.when(userRepository.findAll()).thenReturn(new ArrayList<>());
        userService.findAll();
        userService.findAll();
        Mockito.verify(userRepository, Mockito.times(2)).findAll();
    }

    @Test
    public void whenFindUserById_thenThrowCustomException() {
        UserService userService = new UserServiceImpl(userRepository);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(
                ObjNotFoundException.class,
                () -> userService.findUserById(1)
        );
    }

    @Test
    public void whenFindUserById_thenCallRepository() {
        UserService userService = new UserServiceImpl(userRepository);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new User()));
        userService.findUserById(1);
        Mockito.verify(userRepository, Mockito.times(1)).findById(Mockito.anyLong());
    }

    @Test
    public void whenCreateUser_thenCallRepository() {
        UserService userService = new UserServiceImpl(userRepository);
        User user = new User();
        Mockito.when(userRepository.save(user)).thenReturn(user);
        userService.create(user);
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @Test
    public void whenUpdateEmail_thenNameEqualToOldValue() {
        UserService userService = new UserServiceImpl(userRepository);
        User userNew = new User();
        userNew.setEmail("NEW_email@mail.ru");
        User userOld = new User(1, "Василий", "email@ya.ru");
        User userExpected = new User(1, "Василий", "NEW_email@mail.ru");
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(userExpected);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(userOld));
        User userUpd  = userService.update(userNew, 1);
        assertThat(userUpd.getId(), equalTo(userOld.getId()));
        assertThat(userUpd.getName(), equalTo(userOld.getName()));
        assertThat(userUpd.getEmail(), equalTo(userNew.getEmail()));
    }

    @Test
    public void whenUpdateName_thenEmailEqualToOldValue() {
        UserService userService = new UserServiceImpl(userRepository);
        User userNew = new User();
        userNew.setName("Иван");
        User userOld = new User(1, "Василий", "email@ya.ru");
        User userExpected = new User(1, "Иван", "email@ya.ru");
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(userExpected);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(userOld));
        User userUpd  = userService.update(userNew, 1);
        assertThat(userUpd.getId(), equalTo(userExpected.getId()));
        assertThat(userUpd.getName(), equalTo(userExpected.getName()));
        assertThat(userUpd.getEmail(), equalTo(userExpected.getEmail()));
    }

    @Test
    public void whenUpdateNameAndEmail() {
        UserService userService = new UserServiceImpl(userRepository);
        User userNew = new User();
        userNew.setName("Иван");
        userNew.setEmail("NEW_email@ram.ru");
        User userOld = new User(1, "Василий", "email@ya.ru");
        User userExpected = new User(1, "Иван", "NEW_email@ram.ru");
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(userExpected);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(userOld));
        User userUpd  = userService.update(userNew, 1);
        assertThat(userUpd.getId(), equalTo(userExpected.getId()));
        assertThat(userUpd.getName(), equalTo(userExpected.getName()));
        assertThat(userUpd.getEmail(), equalTo(userExpected.getEmail()));
    }

    @Test
    public void whenDeleteUser_thenCallRepository() {
        UserService userService = new UserServiceImpl(userRepository);
        userService.delete(1L);
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    public void createUser() {
        User user = new User(1L, "Tamara", "tamara@ya.ru");
        userService.create(user);

        TypedQuery<User> query = em.createQuery("Select u from User AS u where u.id = :id", User.class);
        User userFromDB = query.setParameter("id",1L).getSingleResult();

        assertThat(userFromDB.getId(), notNullValue());
        assertThat(userFromDB.getName(), equalTo(user.getName()));
        assertThat(userFromDB.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    public void updateUser() {
        User user = new User(1L, "Tamara", "tamara@ya.ru");
        userService.create(user);

        User userUpd = new User(1L, "Tamara Mihailovna", "tamara@ya.ru");
        userService.update(userUpd, 1L);

        TypedQuery<User> query = em.createQuery("Select u from User AS u where u.id = :id", User.class);
        User userFromDB = query.setParameter("id",1L).getSingleResult();

        assertThat(userFromDB.getId(), notNullValue());
        assertThat(userFromDB.getName(), equalTo(userUpd.getName()));
        assertThat(userFromDB.getEmail(), equalTo(userUpd.getEmail()));
    }
}