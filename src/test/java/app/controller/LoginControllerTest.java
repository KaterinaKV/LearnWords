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
class LoginControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void showLoginForm() {
        ResponseEntity<String> response = testRestTemplate.getForEntity("/login", String.class);

        assertThat(response.getStatusCodeValue(), is(200));
        assertThat(response.getBody(), containsString("<title>SingIn</title>"));
        assertThat(response.getBody(), containsString("<p class=\"error\"></p>"));
    }

    @Test
    void showLoginFormWithError() {
        ResponseEntity<String> response = testRestTemplate.getForEntity("/login?error=true", String.class);

        assertThat(response.getStatusCodeValue(), is(200));
        assertThat(response.getBody(), containsString("<title>SingIn</title>"));
        assertThat(response.getBody(), containsString("<p class=\"error\">true</p>"));
    }
}