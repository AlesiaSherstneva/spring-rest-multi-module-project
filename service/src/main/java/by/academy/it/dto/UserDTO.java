package by.academy.it.dto;

import by.academy.it.pojos.Role;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UserDTO {
    @NotNull(message = "Lastname should not be empty")
    @Size(max = 40, message = "Lastname should not be greater than 40 symbols")
    @Pattern(regexp = "[A-Z][a-z]+", message = "Lastname should contain latin characters only")
    @Column(name = "lastname")
    private String lastname;

    @NotNull(message = "Firstname should not be empty")
    @Size(max = 20, message = "Firstname should not be greater than 20 symbols")
    @Pattern(regexp = "[A-Z][a-z]+", message = "Firstname should contain latin characters only")
    @Column(name = "firstname")
    private String firstname;

    @NotNull(message = "Patronymic should not be empty")
    @Size(max = 40, message = "Patronymic should not be greater than 40 symbols")
    @Pattern(regexp = "[A-Z][a-z]+", message = "Patronymic should contain latin characters only")
    @Column(name = "patronymic")
    private String patronymic;

    @NotNull(message = "Email should not be empty")
    @Email
    @Column(name = "email")
    private String email;

    @NotNull(message = "Role should not be empty")
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;
}