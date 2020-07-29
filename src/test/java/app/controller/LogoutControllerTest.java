package app.controller;

import app.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LogoutControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void logoutAndGetLoginPage() {
        ResponseEntity<String> response = testRestTemplate.getForEntity("/logout", String.class);

        assertThat(response.getStatusCodeValue(), is(200));
        assertThat(response.getBody(), containsString("<title>SingIn</title>"));
    }

}


/*@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
//@WithMockCustomUser
class LogoutControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;

    @Test
    void logoutAndGetLoginPage() throws Exception {
        UserDto user = new UserDto();
        user.setId(1L);
        user.setUsername("login");
        user.setPassword("password");
        userService.add(user);
        mockMvc.perform(get("/logout"))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/login"))
                .andExpect(view().name("redirect:/login"))
                .andExpect(result -> containsString("<title>SingIn</title>"));
    }
}*/
