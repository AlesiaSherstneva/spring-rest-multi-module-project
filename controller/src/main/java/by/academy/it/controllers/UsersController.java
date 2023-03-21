package by.academy.it.controllers;

import by.academy.it.dto.UserDTO;
import by.academy.it.exceptions.ErrorResponse;
import by.academy.it.exceptions.UserNotCreatedException;
import by.academy.it.pojos.Role;
import by.academy.it.pojos.User;
import by.academy.it.services.UserService;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UsersController {
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public UsersController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping()
    public List<UserDTO> showAllUsers() {
        return userService.findAll()
                .stream()
                .map(this::convertToUserDTO)
                .sorted(Comparator.comparing(UserDTO::getEmail))
                .collect(Collectors.toList());
    }

    @PostMapping()
    public ResponseEntity<HttpStatus> addNewUser(@RequestBody @Valid UserDTO userDTO,
                                                 BindingResult bindingResult) {
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
        User user = convertToUser(userDTO);
        userService.saveUser(user);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handlePatternException(UserNotCreatedException exception) {
        ErrorResponse response = new ErrorResponse(exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidFormatException.class)
    private ResponseEntity<ErrorResponse> handleRoleException() {
        ErrorResponse response = new ErrorResponse("Role should have one of values: " + Arrays.toString(Role.values()));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private User convertToUser(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

    private UserDTO convertToUserDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }
}