package app.dao;

import app.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername_Success() {
        User user = new User();
        user.setUsername("login");
        user.setPassword("password");
        entityManager.persistAndFlush(user);

        User foundUser = userRepository.findByUsername("login");

        assertEquals(user, foundUser);
    }

    @Test
    void findByUsername_Failed() {
        User foundUser = userRepository.findByUsername("login");

        assertNull(foundUser);
    }
}