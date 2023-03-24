package by.academy.it.controllers;

import by.academy.it.ControllerApplication;
import by.academy.it.pojos.Role;
import by.academy.it.pojos.User;
import by.academy.it.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ControllerApplication.class)
public class UserControllerTest {
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private UsersController usersController;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setLastname("Testlastname");
        testUser.setFirstname("Testfirstname");
        testUser.setPatronymic("Testpatronymic");
        testUser.setEmail("test@test.test");
        testUser.setRole(Role.CUSTOMER_USER);

        mockMvc = MockMvcBuilders.standaloneSetup(usersController).build();
    }

    @Test
    public void showAllUsersTest() throws Exception {
        List<User> usersInBase = new ArrayList<>(Collections.singletonList(testUser));
        when(userService.findAndPageAll(anyInt())).thenReturn(usersInBase);

        // getting users from default page
        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(1)),
                        jsonPath("$[0].fullname", is("Testlastname Testfirstname Testpatronymic")),
                        jsonPath("$[0].email", is("test@test.test")),
                        jsonPath("$[0].role", is("CUSTOMER_USER")));

        // getting users with request parameter
        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .param("page", String.valueOf(new Random().nextInt(100))))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(1)),
                        jsonPath("$[0].fullname", is("Testlastname Testfirstname Testpatronymic")),
                        jsonPath("$[0].email", is("test@test.test")),
                        jsonPath("$[0].role", is("CUSTOMER_USER")));

        verify(userService, times(2)).findAndPageAll(anyInt());
    }

    @Test
    public void createNewUserTest() throws Exception {
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(MockMvcRequestBuilders.post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testUser)))
                    .andExpectAll(
                            status().isOk(),
                            content().contentType(MediaType.APPLICATION_JSON));
        }

        verify(userService, times(3)).saveUser(any(User.class));
    }

    @Test
    public void createNewUserWithWrongRoleTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)
                        .replaceAll("CUSTOMER_USER", "WRONG_USER")))
                .andExpectAll(
                        status().is4xxClientError(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.status", is(400)),
                        jsonPath("$.message", is("Role should have one of values: " +
                                "[ADMINISTRATOR, SALE_USER, CUSTOMER_USER, SECURE_API_USER]"))
                );
    }

    @Test
    public void createNewUserWithWrongFieldsTest() throws Exception {
        // set empty lastname
        testUser.setLastname("");
        // set firstname more than 20 characters
        testUser.setFirstname("A" + "a".repeat(20));
        // set patronymic with numbers
        testUser.setPatronymic("1234567890");
        // set wrong email
        testUser.setEmail("wrong email");

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpectAll(
                        status().is4xxClientError(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.status", is(400)),
                        jsonPath("$.message", containsString("lastname")),
                        jsonPath("$.message", containsString("firstname")),
                        jsonPath("$.message", containsString("patronymic")),
                        jsonPath("$.message", containsString("email"))
                );
    }

    @AfterEach
    public void tearDown() {
        verifyNoMoreInteractions(userService);
    }
}