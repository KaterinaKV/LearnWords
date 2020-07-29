package app.dao;

import app.model.Catalog;
import app.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DataJpaTest
class CatalogRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CatalogRepository catalogRepository;

    private final TestData testData = new TestData();

    @Test
    void findByNameAndUser_Success() {
        User user = testData.getUser();
        Catalog catalog = new Catalog();
        catalog.setName("catalog");
        catalog.setUser(user);
        entityManager.persistAndFlush(user);
        entityManager.persistAndFlush(catalog);

        Catalog foundCatalog = catalogRepository.findByNameAndUser(catalog.getName(), user);

        assertEquals(catalog, foundCatalog);
    }

    @Test
    void findByNameAndUser_Failed() {
        User user = testData.getUser();
        entityManager.persistAndFlush(user);

        Catalog foundCatalog = catalogRepository.findByNameAndUser("Not Exist", user);

        assertNull(foundCatalog);
    }

    @Test
    void findAllByUser_Success() {
        User user = testData.getUser();
        Catalog catalog = new Catalog();
        catalog.setName("catalog");
        catalog.setUser(user);
        entityManager.persistAndFlush(user);
        entityManager.persistAndFlush(catalog);

        List<Catalog> listCatalog = catalogRepository.findAllByUser(user);

        assertEquals(1, listCatalog.size());
        assertEquals(catalog, listCatalog.get(0));
    }

    @Test
    void findAllByUser_Failed() {
        User user = testData.getUser();
        entityManager.persistAndFlush(user);

        List<Catalog> listCatalog = catalogRepository.findAllByUser(user);

        assertEquals(0, listCatalog.size());
    }

    static class TestData {

        private User getUser() {
            User user = new User();
            user.setUsername("login");
            user.setPassword("password");
            return user;
        }
    }
}