package by.academy.it.pojos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

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