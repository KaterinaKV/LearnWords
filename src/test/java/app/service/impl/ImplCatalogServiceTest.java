package app.service.impl;

import app.dao.CatalogRepository;
import app.dto.CatalogDto;
import app.dto.UserDto;
import app.exception.InvalidInputDataException;
import app.model.Card;
import app.model.Catalog;
import app.model.User;
import app.service.UserService;
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
class ImplCatalogServiceTest {

    @Mock
    private CatalogRepository mockCatalogRepository;
    @Mock
    private UserService mockUserService;
    @InjectMocks
    private ImplCatalogService catalogService;
    private final TestData testData = new TestData();

    @Nested
    class AddCatalog {
        @Test
        void addCatalog_Success() {
            when(mockUserService.convertToEntity(testData.getUserDto())).thenReturn(testData.getUser());
            when(mockUserService.convertToDto(testData.getUser())).thenReturn(testData.getUserDto());
            Catalog catalogToAdd = testData.getCatalog();
            when(mockCatalogRepository.save(catalogToAdd)).thenReturn(catalogToAdd);

            CatalogDto addedCatalog = catalogService.add(testData.getCatalogDto());

            assertNotNull(addedCatalog);
            isCatalogsEquals(addedCatalog, catalogToAdd);
            verify(mockCatalogRepository, times(1)).save(catalogToAdd);
        }

        @Test
        void addCatalog_Failed_NameIsEmpty() {
            CatalogDto catalogWithEmptyName = testData.getCatalogDtoWithEmptyName();

            Throwable thrown = assertThrows(InvalidInputDataException.class, () -> catalogService.add(catalogWithEmptyName));
            assertNotNull(thrown.getMessage());
            assertEquals("Name catalog is required.", thrown.getMessage());
        }

        @Test
        void addCatalog_Failed_NameContainsUnauthorizedSymbols() {
            CatalogDto catalogWithIncorrectName = testData.getCatalogDtoWithIncorrectName();

            Throwable thrown = assertThrows(InvalidInputDataException.class, () -> catalogService.add(catalogWithIncorrectName));
            assertNotNull(thrown.getMessage());
            assertEquals("Name catalog contains unauthorized symbols.", thrown.getMessage());
        }

        @Test
        void addCatalog_Failed_CatalogAlreadyExist() {
            when(mockUserService.convertToEntity(testData.getUserDto())).thenReturn(testData.getUser());
            when(mockUserService.convertToDto(testData.getUser())).thenReturn(testData.getUserDto());
            when(mockCatalogRepository.findAllByUser(testData.getUser())).thenReturn(testData.getCatalogList());

            Throwable thrown = assertThrows(InvalidInputDataException.class, () -> catalogService.add(testData.getCatalogDto()));
            assertNotNull(thrown.getMessage());
            assertEquals("Such catalog already exists.", thrown.getMessage());
            verify(mockCatalogRepository, times(1)).findAllByUser(testData.getUser());
        }
    }

    @Test
    void findByNameAndUser() {
        when(mockUserService.convertToEntity(testData.getUserDto())).thenReturn(testData.getUser());
        when(mockUserService.convertToDto(testData.getUser())).thenReturn(testData.getUserDto());

        when(mockCatalogRepository.findByNameAndUser(testData.getCatalog().getName(), testData.getUser()))
                .thenReturn(testData.getCatalog());

        CatalogDto catalogDto = catalogService.findByNameAndUser(testData.getCatalogDto().getName(), testData.getUserDto());

        assertEquals(catalogDto, testData.getCatalogDto());
        verify(mockCatalogRepository, times(1))
                .findByNameAndUser(testData.getCatalog().getName(), testData.getUser());
    }

    @Nested
    class FindCatalogById {
        @Test
        void findById_Success() {
            when(mockUserService.convertToDto(testData.getUser())).thenReturn(testData.getUserDto());
            Catalog catalogToFind = testData.getCatalog();
            when(mockCatalogRepository.findById(1L)).thenReturn(Optional.of(catalogToFind));

            CatalogDto foundCatalogDto = catalogService.findById(1L);

            assertNotNull(foundCatalogDto);
            isCatalogsEquals(foundCatalogDto, catalogToFind);
            verify(mockCatalogRepository, times(1)).findById(1L);
        }

        @Test
        void findById_Failed() {
            when(mockCatalogRepository.findById(0L)).thenReturn(Optional.empty());

            CatalogDto foundCatalogDto = catalogService.findById(0L);

            assertNull(foundCatalogDto);
            verify(mockCatalogRepository, times(1)).findById(0L);
        }
    }

    @Test
    void update() {
        when(mockUserService.convertToEntity(testData.getUserDto())).thenReturn(testData.getUser());
        when(mockUserService.convertToDto(testData.getUser())).thenReturn(testData.getUserDto());
        when(mockCatalogRepository.findById(testData.getCatalog().getId())).thenReturn(java.util.Optional.of(testData.getCatalog()));

        catalogService.update(testData.getCatalogDto());

        verify(mockCatalogRepository, times(1)).save(testData.getCatalog());
    }

    @Test
    void delete() {
        catalogService.delete(testData.getCatalogDto());

        verify(mockCatalogRepository, times(1)).deleteById(testData.getCatalog().getId());
    }

    @Nested
    class FindAllCatalogsByUsers {
        @Test
        void findAllByUser_Success() {
            List<Catalog> catalogs = testData.getCatalogList();
            when(mockUserService.convertToEntity(testData.getUserDto())).thenReturn(testData.getUser());
            when(mockUserService.convertToDto(testData.getUser())).thenReturn(testData.getUserDto());
            when(mockCatalogRepository.findAllByUser(testData.getUser())).thenReturn(catalogs);

            List<CatalogDto> catalogsDto = catalogService.findAllByUser(testData.getUserDto());

            assertNotEquals(0, catalogsDto.size());
            isCatalogListsEquals(catalogsDto, catalogs);
            verify(mockCatalogRepository, times(1)).findAllByUser(testData.getUser());
        }

        @Test
        void findAllByUser_Failed() {
            when(mockUserService.convertToEntity(testData.getUserDto())).thenReturn(testData.getUser());
            when(mockCatalogRepository.findAllByUser(testData.getUser())).thenReturn(new ArrayList<>());

            List<CatalogDto> catalogsDto = catalogService.findAllByUser(testData.getUserDto());

            assertEquals(catalogsDto.size(), 0);
            verify(mockCatalogRepository, times(1)).findAllByUser(testData.getUser());
        }
    }

    @Nested
    class Convert {

        @Test
        void convertToDto() {
            when(mockUserService.convertToDto(testData.getUser())).thenReturn(testData.getUserDto());
            Catalog catalogToConvert = testData.getCatalog();

            CatalogDto convertedCatalogDto = catalogService.convertToDto(catalogToConvert);

            isCatalogsEquals(convertedCatalogDto, catalogToConvert);
            assertEquals(convertedCatalogDto.getCountCard(), 0);
        }

        @Test
        void convertToDtoWithCardList() {
            when(mockUserService.convertToDto(testData.getUser())).thenReturn(testData.getUserDto());
            Catalog catalogToConvert = testData.getCatalogWithCardList();

            CatalogDto convertedCatalogDto = catalogService.convertToDto(catalogToConvert);

            isCatalogsEquals(convertedCatalogDto, catalogToConvert);
            assertEquals(convertedCatalogDto.getCountCard(), catalogToConvert.getCardList().size());
        }

        @Test
        void convertToEntity() {
            when(mockUserService.convertToEntity(testData.getUserDto())).thenReturn(testData.getUser());
            CatalogDto catalogDtoToConvert = testData.getCatalogDto();

            Catalog convertedCatalog = catalogService.convertToEntity(catalogDtoToConvert);

            isCatalogsEquals(catalogDtoToConvert, convertedCatalog);
        }

    }

    static void isCatalogsEquals(CatalogDto catalogDto, Catalog catalog) {
        assertEquals(catalogDto.getId(), catalog.getId(), "Ids do not match");
        assertEquals(catalogDto.getName(), catalog.getName(), "Names do not match");
        ImplUserServiceTest.isUsersEquals(catalogDto.getUserDto(), catalog.getUser());
    }

    private void isCatalogListsEquals(List<CatalogDto> catalogsDto, List<Catalog> catalogs) {
        assertEquals(catalogsDto.size(), catalogs.size());
        for (int i = 0; i < catalogs.size(); i++) {
            isCatalogsEquals(catalogsDto.get(i), catalogs.get(i));
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

        public CatalogDto getCatalogDtoWithEmptyName() {
            return new CatalogDto(1L, "", getUserDto());
        }

        public CatalogDto getCatalogDtoWithIncorrectName() {
            return new CatalogDto(1L, "cat{}alog1", getUserDto());
        }

        private Catalog getCatalogWithCardList() {
            return new Catalog(1L, "catalog1", getCardList(), getUser());
        }

        private List<Card> getCardList() {
            List<Card> cardList = new ArrayList<>();
            Card card = new Card(1L, "word", "translate", "transcription", getCatalog());
            cardList.add(card);
            return cardList;
        }

        private List<Catalog> getCatalogList() {
            List<Catalog> catalogList = new ArrayList<>();
            catalogList.add(getCatalog());
            return catalogList;
        }
    }
}