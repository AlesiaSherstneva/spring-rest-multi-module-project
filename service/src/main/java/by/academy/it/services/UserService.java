package by.academy.it.services;

import by.academy.it.pojos.User;
import by.academy.it.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAndPageAll(int page) {
        LOGGER.trace("Entering findAndPageAll() method");
        LOGGER.debug("Showing all users (in User pattern) from " + page + " page");

        List<User> receivedUsers =  userRepository.findAll(PageRequest.of(page - 1, 10, Sort.by("email")))
                .getContent();

        LOGGER.info("Returned " + (receivedUsers.size() > 0 ? receivedUsers.size() + " users" : "empty page"));

        return receivedUsers;
    }

    public void saveUser(User user) {
        LOGGER.trace("Entering saveUser() method");
        LOGGER.debug("Saving new user with email " + user.getEmail());

        userRepository.save(user);

        LOGGER.info("User with email " + user.getEmail() + "was successfully saved");
    }
}