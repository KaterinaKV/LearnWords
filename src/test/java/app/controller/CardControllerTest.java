package app.controller;

import app.config.TestSecurityConfig;
import app.config.WithMockCustomUser;
import app.dto.CardDto;
import app.dto.CatalogDto;
import app.dto.UserDto;
import app.service.CardService;
import app.service.CatalogService;
import app.service.UserService;
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
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private CardService cardService;

    private final TestData testData = new TestData();

    @Test
    void getCatalogPage() throws Exception {
        userService.add(testData.getUserDto());
        catalogService.add(testData.getCatalogDto());
        cardService.add(testData.getCardDto());

        mockMvc.perform(get("/catalog/card/add?catalogName=catalog"))
                .andExpect(status().isOk())
                .andExpect(view().name("catalog"))
                .andExpect(model().attributeExists("cardDto"))
                .andExpect(model().attributeExists("catalogName"))
                .andExpect(result -> containsString("<title>Catalog"));
    }

    @Test
    void processAddCard_Success() throws Exception {
        userService.add(testData.getUserDto());
        catalogService.add(testData.getCatalogDto());

        mockMvc.perform(post("/catalog/card/add?catalogName=catalog")
                .param("id", "1")
                .param("word", "word")
                .param("translation", "translation")
                .param("transcription", "transcription")
                .flashAttr("cardDto", new CardDto()))
                .andExpect(status().is(302))
                .andExpect(view().name("redirect:/catalog/show?catalogName=catalog"))
                .andExpect(result -> containsString("<title>Catalog"));
    }

    @Test
    void processAddCard_Failed() throws Exception {
        userService.add(testData.getUserDto());
        catalogService.add(testData.getCatalogDto());

        mockMvc.perform(post("/catalog/card/add?catalogName=catalog")
                .param("word", "wo486rd")
                .param("translation", "translation")
                .param("transcription", "transcription")
                .flashAttr("cardDto", new CardDto()))
                .andExpect(status().isOk())
                .andExpect(view().name("catalog"))
                .andExpect(model().attributeExists("cardDto"))
                .andExpect(model().attributeExists("catalogName"))
                .andExpect(result -> containsString("<title>Catalog"));
    }

    @Test
    void getUpdateCardForm() throws Exception {
        userService.add(testData.getUserDto());
        catalogService.add(testData.getCatalogDto());
        cardService.add(testData.getCardDto());

        mockMvc.perform(get("/catalog/card/update?id=1"))
                .andExpect(status().isOk())
                .andExpect(view().name("catalog"))
                .andExpect(model().attributeExists("cardDto"))
                .andExpect(model().attributeExists("catalogName"))
                .andExpect(result -> containsString("<title>Catalog"));
    }

    @Test
    void processUpdateCard_Success() throws Exception {
        userService.add(testData.getUserDto());
        catalogService.add(testData.getCatalogDto());
        cardService.add(testData.getCardDto());

        mockMvc.perform(post("/catalog/card/update?catalogName=catalog")
                .param("id", "1")
                .param("word", "word")
                .param("translation", "translation")
                .param("transcription", "transcription")
                .flashAttr("cardDto", new CardDto()))
                .andExpect(status().is(302))
                .andExpect(view().name("redirect:/catalog/show?catalogName=catalog"))
                .andExpect(result -> containsString("<title>Catalog"));
    }

    @Test
    void processUpdateCard_Failed() throws Exception {
        userService.add(testData.getUserDto());
        catalogService.add(testData.getCatalogDto());
        cardService.add(testData.getCardDto());

        mockMvc.perform(post("/catalog/card/update?catalogName=catalog")
                .param("id", "1")
                .param("word", "wo789rd")
                .param("translation", "translation")
                .param("transcription", "transcription")
                .flashAttr("cardDto", new CardDto()))
                .andExpect(status().isOk())
                .andExpect(view().name("catalog"))
                .andExpect(model().attributeExists("cardDto"))
                .andExpect(model().attributeExists("catalogName"))
                .andExpect(result -> containsString("<title>Learn Cards</title>"));
    }

    @Test
    void processDeleteCard() throws Exception {
        userService.add(testData.getUserDto());
        catalogService.add(testData.getCatalogDto());
        cardService.add(testData.getCardDto());

        mockMvc.perform(get("/catalog/card/delete?id=1"))
                .andExpect(status().is(302))
                .andExpect(view().name("redirect:/catalog/show?catalogName=catalog"))
                .andExpect(result -> containsString("<title>Catalog"));
    }

    private static class TestData {
        private UserDto getUserDto() {
            return new UserDto(1L, "login", "password");
        }

        private CatalogDto getCatalogDto() {
            return new CatalogDto(1L, "catalog", getUserDto());
        }

        private CardDto getCardDto() {
            return new CardDto(1L, "word", "translate", "transcription", getCatalogDto());
        }
    }
}