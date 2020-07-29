package app.service.impl;

import app.dao.CardRepository;
import app.dto.CardDto;
import app.dto.CatalogDto;
import app.dto.UserDto;
import app.exception.InvalidInputDataException;
import app.model.Card;
import app.model.Catalog;
import app.model.User;
import app.service.CatalogService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImplCardServiceTest {

    @Mock
    private CardRepository mockCardRepository;
    @Mock
    private CatalogService mockCatalogService;
    @InjectMocks
    private ImplCardService cardService;
    private final TestData testData = new TestData();

    @Nested
    class AddCard {
        @Test
        void addCard_Success() {
            when(mockCatalogService.convertToEntity(testData.getCatalogDto())).thenReturn(testData.getCatalog());
            when(mockCatalogService.convertToDto(testData.getCatalog())).thenReturn(testData.getCatalogDto());
            Card cardToAdd = testData.getCard();
            when(mockCardRepository.save(cardToAdd)).thenReturn(cardToAdd);

            CardDto addedCard = cardService.add(testData.getCardDto());

            assertNotNull(addedCard);
            isCardsEquals(addedCard, cardToAdd);
            verify(mockCardRepository, times(1)).save(cardToAdd);
        }

        @Test
        void addCard_Failed_WordIsEmpty() {
            CardDto cardWithEmptyWord = testData.getCardDtoWithEmptyWord();

            Throwable thrown = assertThrows(InvalidInputDataException.class, () -> cardService.add(cardWithEmptyWord));
            assertNotNull(thrown.getMessage());
            assertEquals("Word is required.", thrown.getMessage());
        }

        @Test
        void addCard_Failed_TranslationIsEmpty() {
            CardDto cardWithEmptyTranslation = testData.getCardDtoWithEmptyTranslation();

            Throwable thrown = assertThrows(InvalidInputDataException.class, () -> cardService.add(cardWithEmptyTranslation));
            assertNotNull(thrown.getMessage());
            assertEquals("Translation is required.", thrown.getMessage());
        }

        @Test
        void addCard_Failed_WordContainsUnauthorizedSymbols() {
            CardDto cardWithIncorrectWord = testData.getCardDtoWithIncorrectWord();

            Throwable thrown = assertThrows(InvalidInputDataException.class, () -> cardService.add(cardWithIncorrectWord));
            assertNotNull(thrown.getMessage());
            assertEquals("The \"Word\" field contains unauthorized symbols.", thrown.getMessage());
        }

        @Test
        void addCard_Failed_TranslationContainsUnauthorizedSymbols() {
            CardDto cardWithIncorrectTranslation = testData.getCardDtoWithIncorrectTranslation();

            Throwable thrown = assertThrows(InvalidInputDataException.class, () -> cardService.add(cardWithIncorrectTranslation));
            assertNotNull(thrown.getMessage());
            assertEquals("The \"Translation\" field contains unauthorized symbols.", thrown.getMessage());
        }

        @Test
        void addCard_Failed_CardAlreadyExist() {
            when(mockCatalogService.convertToEntity(testData.getCatalogDto())).thenReturn(testData.getCatalog());
            when(mockCatalogService.convertToDto(testData.getCatalog())).thenReturn(testData.getCatalogDto());
            when(mockCardRepository.findAllByCatalog(testData.getCatalog())).thenReturn(testData.getCardList());

            Throwable thrown = assertThrows(InvalidInputDataException.class, () -> cardService.add(testData.getCardDto()));
            assertNotNull(thrown.getMessage());
            assertEquals("Such card already exists.", thrown.getMessage());
            verify(mockCardRepository, times(1)).findAllByCatalog(testData.getCatalog());
        }
    }

    @Nested
    class FindCardById {
        @Test
        void findById_Success() {
            Card cardToFind = testData.getCard();
            when(mockCatalogService.convertToDto(testData.getCatalog())).thenReturn(testData.getCatalogDto());
            when(mockCardRepository.findById(1L)).thenReturn(Optional.of(cardToFind));

            CardDto foundCardDto = cardService.findById(1L);

            assertNotNull(foundCardDto);
            isCardsEquals(foundCardDto, cardToFind);
            verify(mockCardRepository, times(1)).findById(1L);
        }

        @Test
        void findById_Failed() {
            when(mockCardRepository.findById(0L)).thenReturn(Optional.empty());

            CardDto foundCardDto = cardService.findById(0L);

            assertNull(foundCardDto);
            verify(mockCardRepository, times(1)).findById(0L);
        }
    }

    @Test
    void update() {
        when(mockCatalogService.convertToEntity(testData.getCatalogDto())).thenReturn(testData.getCatalog());
        when(mockCatalogService.convertToDto(testData.getCatalog())).thenReturn(testData.getCatalogDto());
        when(mockCardRepository.findById(testData.getCatalog().getId())).thenReturn(java.util.Optional.of(testData.getCard()));

        cardService.update(testData.getCardDto());

        verify(mockCardRepository, times(1)).save(testData.getCard());
    }

    @Test
    void delete() {
        cardService.delete(testData.getCardDto().getId());

        verify(mockCardRepository, times(1)).deleteById(testData.getCard().getId());
    }

    @Nested
    class FindAllCards {
        @Test
        void findAll_Success() {
            List<Card> cards = testData.getCardList();
            when(mockCatalogService.convertToDto(testData.getCatalog())).thenReturn(testData.getCatalogDto());
            when(mockCardRepository.findAll()).thenReturn(cards);

            List<CardDto> cardsDto = cardService.findAll();

            assertNotEquals(0, cardsDto.size());
            isCardListsEquals(cardsDto, cards);
            verify(mockCardRepository, times(1)).findAll();
        }

        @Test
        void findAll_Failed() {
            when(mockCardRepository.findAll()).thenReturn(new ArrayList<>());

            assertEquals(cardService.findAll().size(), 0);
            verify(mockCardRepository, times(1)).findAll();
        }
    }

    @Nested
    class FindAllByCatalog {
        @Test
        void findAllByCatalog_Success() {
            List<Card> cards = testData.getCardList();
            when(mockCatalogService.convertToEntity(testData.getCatalogDto())).thenReturn(testData.getCatalog());
            when(mockCatalogService.convertToDto(testData.getCatalog())).thenReturn(testData.getCatalogDto());
            when(mockCardRepository.findAllByCatalog(testData.getCatalog())).thenReturn(cards);

            List<CardDto> cardsDto = cardService.findAllByCatalog(testData.getCatalogDto());

            assertNotEquals(0, cardsDto.size());
            isCardListsEquals(cardsDto, cards);
            verify(mockCardRepository, times(1)).findAllByCatalog(testData.getCatalog());
        }

        @Test
        void findAllByUser_Failed() {
            when(mockCatalogService.convertToEntity(testData.getCatalogDto())).thenReturn(testData.getCatalog());
            when(mockCardRepository.findAllByCatalog(testData.getCatalog())).thenReturn(new ArrayList<>());

            List<CardDto> cardsDto = cardService.findAllByCatalog(testData.getCatalogDto());

            assertEquals(cardsDto.size(), 0);
            verify(mockCardRepository, times(1)).findAllByCatalog(testData.getCatalog());
        }
    }

    @Nested
    class Convert {

        @Test
        void convertToDto() {
            when(mockCatalogService.convertToDto(testData.getCatalog())).thenReturn(testData.getCatalogDto());
            Card cardToConvert = testData.getCard();

            CardDto convertedCardDto = cardService.convertToDto(cardToConvert);

            isCardsEquals(convertedCardDto, cardToConvert);
        }

        @Test
        void convertToEntity() {
            when(mockCatalogService.convertToEntity(testData.getCatalogDto())).thenReturn(testData.getCatalog());
            CardDto cardDtoToConvert = testData.getCardDto();

            Card convertedCard = cardService.convertToEntity(cardDtoToConvert);

            isCardsEquals(cardDtoToConvert, convertedCard);
        }
    }

    static void isCardsEquals(CardDto cardDto, Card card) {
        assertEquals(cardDto.getId(), card.getId(), "Ids do not match");
        assertEquals(cardDto.getWord(), card.getWord(), "Names do not match");
        ImplCatalogServiceTest.isCatalogsEquals(cardDto.getCatalogDto(), card.getCatalog());
    }

    private void isCardListsEquals(List<CardDto> cardsDto, List<Card> cards) {
        assertEquals(cardsDto.size(), cards.size());
        for (int i = 0; i < cards.size(); i++) {
            isCardsEquals(cardsDto.get(i), cards.get(i));
        }
    }

    private static class TestData {
        private User getUser() {
            return new User(1L, "login", "password");
        }

        private UserDto getUserDto() {
            return new UserDto(1L, "login", "password");
        }

        private Catalog getCatalog() {
            return new Catalog(1L, "catalog1", getUser());
        }

        private CatalogDto getCatalogDto() {
            return new CatalogDto(1L, "catalog1", getUserDto());
        }

        public Card getCard() {
            return new Card(1L, "word", "translate", "transcription", getCatalog());
        }

        public CardDto getCardDto() {
            return new CardDto(1L, "word", "translate", "transcription", getCatalogDto());
        }

        public CardDto getCardDtoWithEmptyWord() {
            return new CardDto(1L, "", "translate", "transcription", getCatalogDto());
        }

        public CardDto getCardDtoWithEmptyTranslation() {
            return new CardDto(1L, "word", "", "transcription", getCatalogDto());
        }
        public CardDto getCardDtoWithIncorrectWord() {
            return new CardDto(1L, "wo132{}rd", "translate", "transcription", getCatalogDto());
        }
        public CardDto getCardDtoWithIncorrectTranslation() {
            return new CardDto(1L, "word", "tran4567()slate", "transcription", getCatalogDto());
        }

        private List<Card> getCardList() {
            List<Card> cardList = new ArrayList<>();
            Card card = new Card(1L, "word", "translate", "transcription", getCatalog());
            cardList.add(card);
            return cardList;
        }
    }
}