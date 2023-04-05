package by.academy.it.services;

import by.academy.it.pojos.User;
import by.academy.it.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {
    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    public void findAllUsersTest() {
        List<User> usersInBase = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setEmail("test-" + i + "@test.test");
            usersInBase.add(user);
        }
        when(userRepository.findAll(isA(Pageable.class))).thenReturn(new PageImpl<>(usersInBase));

        List<User> receivedUsers = userService.findAndPageAll(new Random().nextInt(100));

        assertEquals(10, receivedUsers.size());
        assertEquals("test-0@test.test", receivedUsers.get(0).getEmail());
        assertEquals("test-9@test.test", receivedUsers.get(receivedUsers.size() - 1).getEmail());

        verify(userRepository, times(1)).findAll(isA(Pageable.class));
    }

    @Test
    public void saveUserTest() {
        for (int i = 0; i < 7; i++) userRepository.save(new User());
        verify(userRepository, times(7)).save(any(User.class));
    }

    @AfterEach
    public void tearDown() {
        verifyNoMoreInteractions(userRepository);
    }
}