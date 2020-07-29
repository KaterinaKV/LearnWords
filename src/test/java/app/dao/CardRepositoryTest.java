package app.dao;

import app.model.Card;
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
class CardRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CardRepository cardRepository;

    private final TestData testData = new TestData();

    @Test
    void findAllByCatalog_Success() {
        User user = testData.getUser();
        Catalog catalog = new Catalog();
        catalog.setName("catalog");
        catalog.setUser(user);
        Card card = new Card();
        card.setWord("word");
        card.setTranslation("слово");
        card.setCatalog(catalog);
        entityManager.persistAndFlush(user);
        entityManager.persistAndFlush(catalog);
        entityManager.persistAndFlush(card);

        List<Card> listCards = cardRepository.findAllByCatalog(catalog);

        assertEquals(1, listCards.size());
        assertEquals(card, listCards.get(0));
    }

    @Test
    void findAllByCatalog_Failed() {
        User user = testData.getUser();
        Catalog catalog = new Catalog();
        catalog.setName("catalog");
        catalog.setUser(user);
        entityManager.persistAndFlush(user);
        entityManager.persistAndFlush(catalog);

        List<Card> listCards = cardRepository.findAllByCatalog(catalog);

        assertEquals(0, listCards.size());
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