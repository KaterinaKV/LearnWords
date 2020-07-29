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
class LearnCardsControllerTest {

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
    void processLearnCards_StartList() throws Exception {
        userService.add(testData.getUserDto());
        catalogService.add(testData.getCatalogDto());
        cardService.add(testData.getCardDto());

        mockMvc.perform(get("/catalog/learn?catalogName=catalog&isWord=true"))
                .andExpect(status().isOk())
                .andExpect(view().name("learnCard"))
                .andExpect(result -> containsString("<title>Learn Cards</title>"));
    }

    @Test
    void processLearnCards_ContinueList() throws Exception {
        userService.add(testData.getUserDto());
        catalogService.add(testData.getCatalogDto());
        cardService.add(testData.getCardDto());
        cardService.add(testData.getCardDto2());

        mockMvc.perform(get("/catalog/learn?catalogName=catalog&i=1&isWord=false&order=123456"))
                .andExpect(status().isOk())
                .andExpect(view().name("learnCard"))
                .andExpect(result -> containsString("<title>Learn Cards</title>"));
    }

    @Test
    void processLearnCards_StopList() throws Exception {
        userService.add(testData.getUserDto());
        catalogService.add(testData.getCatalogDto());
        cardService.add(testData.getCardDto());
        cardService.add(testData.getCardDto2());

        mockMvc.perform(get("/catalog/learn?catalogName=catalog&i=2&isWord=true&order=123456"))
                .andExpect(status().isOk())
                .andExpect(view().name("learnCard"))
                .andExpect(result -> containsString("<title>Learn Cards</title>"));
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

        private CardDto getCardDto2() {
            return new CardDto(2L, "wordd", "translatee", "transcriptionn", getCatalogDto());
        }
    }
}