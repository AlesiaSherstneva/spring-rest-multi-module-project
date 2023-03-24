package by.academy.it.controllers;

import by.academy.it.dto.UserDTO;
import by.academy.it.dto.UserMapper;
import by.academy.it.exceptions.ErrorResponse;
import by.academy.it.exceptions.UserNotCreatedException;
import by.academy.it.pojos.Role;
import by.academy.it.pojos.User;
import by.academy.it.services.UserService;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UsersController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UsersController.class);

    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UsersController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping()
    public List<UserDTO> showAllUsers(@RequestParam(value = "page", required = false,
            defaultValue = "1") int page) {
        LOGGER.trace("Entering showAllUsers() method");
        LOGGER.debug("Showing all users (in UserDTO pattern) from " + page + " page");

        List<UserDTO> receivedUserDTOs = userService.findAndPageAll(page)
                .stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());

        LOGGER.info("Returned " + (receivedUserDTOs.size() > 0 ? receivedUserDTOs.size() + " userDTOs" : "empty page"));

        return receivedUserDTOs;
    }

    @PostMapping()
    public ResponseEntity<HttpStatus> createNewUser(@RequestBody @Valid User user,
                                                    BindingResult bindingResult) {
        LOGGER.trace("Entering createNewUser() method");
        LOGGER.debug("Creating new user: lastname - " + user.getLastname() + ", firstname - " + user.getFirstname()
                + ", patronymic - " + user.getPatronymic() + ", email - " + user.getEmail());

        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessage.append(error.getField())
                        .append(" - ")
                        .append(error.getDefaultMessage())
                        .append("; ");
            }
            throw new UserNotCreatedException(errorMessage.toString());
        }

        user.setId(0);
        userService.saveUser(user);

        LOGGER.info("User with email " + user.getEmail() + " was successfully created");

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(UserNotCreatedException exception) {
        LOGGER.error("400 User didn't create, wrong fields");

        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.getMessage(),
                System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidFormatException.class)
    private ResponseEntity<ErrorResponse> handleException() {
        LOGGER.error("400 User didn't create, wrong role");

        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "Role should have one of values: " + Arrays.toString(Role.values()),
                System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private UserDTO convertToUserDTO(User user) {
        return userMapper.mapUserToUserDTO(user);
    }
}