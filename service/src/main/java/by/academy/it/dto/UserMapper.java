package by.academy.it.dto;

import by.academy.it.pojos.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDTO mapUserToUserDTO(User user) {
        return UserDTO.builder()
                .fullname(user.getLastname() + " " + user.getFirstname() + " " + user.getPatronymic())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}