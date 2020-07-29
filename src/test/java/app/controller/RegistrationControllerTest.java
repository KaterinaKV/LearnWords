package app.controller;

import app.config.TestSecurityConfig;
import app.config.WithMockCustomUser;
import app.dao.UserRepository;
import app.dto.UserDto;
import app.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@WithMockCustomUser
@Transactional
@Rollback
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    private final TestData testData = new TestData();

    @Test
    void showRegistrationForm() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("userDto"))
                .andExpect(result -> containsString("<title>SingUp</title>"));
    }

    @Test
    void processRegistrationSuccess() throws Exception {
        mockMvc.perform(testData.getRequest())
                .andExpect(status().is(302))
                .andExpect(view().name("redirect:/login"))
                .andExpect(result -> containsString("<title>SingIn</title>"));
    }

    @Test
    void processRegistrationFailed() throws Exception {
        userRepository.save(testData.getUser());

        mockMvc.perform(testData.getRequest())
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("userDto"))
                .andExpect(result -> containsString("<title>SingUp</title>"))
                .andExpect(result -> containsString("User with this login already exists."));
    }

    private static class TestData {

        private User getUser() {
            User user = new User();
            user.setUsername("login");
            user.setPassword("password");
            return user;
        }

        private RequestBuilder getRequest() {
            return post("/register")
                    .param("username", "login")
                    .param("password", "password")
                    .flashAttr("userDto", new UserDto());
        }
    }
}