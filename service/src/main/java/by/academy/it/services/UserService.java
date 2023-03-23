package by.academy.it.services;

import by.academy.it.pojos.User;
import by.academy.it.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAndPageAll(int page) {
        return userRepository.findAll(PageRequest.of(page, 10, Sort.by("email"))).getContent();
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }
}