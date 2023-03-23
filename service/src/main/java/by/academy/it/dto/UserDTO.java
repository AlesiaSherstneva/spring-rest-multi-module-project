package by.academy.it.dto;

import by.academy.it.pojos.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserDTO {
    private String fullname;
    private String email;
    private Role role;
}