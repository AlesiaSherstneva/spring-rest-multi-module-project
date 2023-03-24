package by.academy.it.integration;

import by.academy.it.ControllerApplication;
import by.academy.it.pojos.Role;
import by.academy.it.pojos.User;
import by.academy.it.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;
import java.util.Random;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(classes = ControllerApplication.class)
@WebAppConfiguration
@TestPropertySource("classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.Random.class)
@Transactional
public class FromEndToEndTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void showTenPeopleFromTheFirstPageTest() throws Exception {
        // getting users from default page
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(10)),
                        jsonPath("$[0].fullname", is("First Test Person")),
                        jsonPath("$[0].email", is("01-test@test.test")),
                        jsonPath("$[9].fullname", is("Tenth Test Person")),
                        jsonPath("$[9].email", is("10-test@test.test")))
                .andReturn();
        String fromRequestWithoutParam = mvcResult.getResponse().getContentAsString();

        // getting users with request parameter
        mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .param("page", "1"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(10)),
                        jsonPath("$[0].fullname", is("First Test Person")),
                        jsonPath("$[0].email", is("01-test@test.test")),
                        jsonPath("$[9].fullname", is("Tenth Test Person")),
                        jsonPath("$[9].email", is("10-test@test.test")))
                .andReturn();
        String fromRequestWithParam = mvcResult.getResponse().getContentAsString();

        assertEquals(fromRequestWithoutParam, fromRequestWithParam);
    }

    @Test
    public void showTwoPeopleFromTheSecondPageTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .param("page", "2"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(2)),
                        jsonPath("$[0].fullname", is("Eleventh Test Person")),
                        jsonPath("$[0].email", is("11-test@test.test")),
                        jsonPath("$[1].fullname", is("Twelfth Test Person")),
                        jsonPath("$[1].email", is("12-test@test.test")));
    }

    @Test
    public void showEmptyResultFromTheOtherPageTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .param("page", String.valueOf(new Random().nextInt(100) + 3)))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(0)));
    }

    @Test
    public void createNewPersonTest() throws Exception {
        User testUser = new User();
        testUser.setLastname("New");
        testUser.setFirstname("Test");
        testUser.setPatronymic("Person");
        testUser.setEmail("test@test.test");
        testUser.setRole(Role.SECURE_API_USER);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON));

        Optional<User> receivedUser = userRepository.findByEmail(testUser.getEmail());
        assertTrue(receivedUser.isPresent());

        assertEquals(testUser.getLastname(), receivedUser.get().getLastname());
        assertEquals(testUser.getFirstname(), receivedUser.get().getFirstname());
        assertEquals(testUser.getPatronymic(), receivedUser.get().getPatronymic());
        assertEquals(testUser.getRole(), receivedUser.get().getRole());
    }
}