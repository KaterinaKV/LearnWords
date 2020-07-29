package app.controller;

import app.config.TestSecurityConfig;
import app.config.WithMockCustomUser;
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
class CatalogControllerTest {

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
    void getCatalogsPage() throws Exception {
        userService.add(testData.getUserDto());
        catalogService.add(testData.getCatalogDto());

        mockMvc.perform(get("/catalog"))
                .andExpect(status().isOk())
                .andExpect(view().name("catalogs"))
                .andExpect(model().attributeExists("catalogDto"))
                .andExpect(model().attributeExists("catalogList"))
                .andExpect(result -> containsString("<title>All catalogs</title>"));
    }

    @Test
    void processAddCatalog_Success() throws Exception {
        userService.add(testData.getUserDto());

        mockMvc.perform(post("/catalog/add")
                .param("id", "1")
                .param("name", "catalog")
                .flashAttr("catalogDto", new CatalogDto()))
                .andExpect(status().is(302))
                .andExpect(view().name("redirect:/catalog"))
                .andExpect(result -> containsString("<title>Catalog"));
    }

    @Test
    void processAddCatalog_Failed() throws Exception {
        userService.add(testData.getUserDto());
        catalogService.add(testData.getCatalogDto());

        mockMvc.perform(post("/catalog/add")
                .param("name", "catalog")
                .flashAttr("catalogDto", new CatalogDto()))
                .andExpect(status().isOk())
                .andExpect(view().name("catalogs"))
                .andExpect(result -> containsString("<title>All catalogs</title>"));
    }

    @Test
    void getShowCatalogPage() throws Exception {
        userService.add(testData.getUserDto());
        catalogService.add(testData.getCatalogDto());

        mockMvc.perform(get("/catalog/show?catalogName=catalog"))
                .andExpect(status().isOk())
                .andExpect(view().name("catalog"))
                .andExpect(model().attributeExists("cardDto"))
                .andExpect(model().attributeExists("catalogName"))
                .andExpect(result -> containsString("<title>Catalog"));
    }

    @Test
    void getUpdateCatalogPage() throws Exception {
        userService.add(testData.getUserDto());
        catalogService.add(testData.getCatalogDto());

        mockMvc.perform(get("/catalog/update?catalogName=catalog"))
                .andExpect(status().isOk())
                .andExpect(view().name("catalogs"))
                .andExpect(model().attributeExists("catalogDto"))
                .andExpect(model().attributeExists("catalogList"))
                .andExpect(result -> containsString("<title>All catalogs</title>"));
    }

    @Test
    void processUpdateCatalog_Success() throws Exception {
        userService.add(testData.getUserDto());
        catalogService.add(testData.getCatalogDto());

        mockMvc.perform(post("/catalog/update")
                .param("id", "1")
                .param("name", "catalog1")
                .flashAttr("catalogDto", new CatalogDto()))
                .andExpect(status().is(302))
                .andExpect(view().name("redirect:/catalog"))
                .andExpect(result -> containsString("<title>Catalog"));
    }

    @Test
    void processUpdateCatalog_Failing() throws Exception {
        userService.add(testData.getUserDto());
        catalogService.add(testData.getCatalogDto());

        mockMvc.perform(post("/catalog/update")
                .param("id", "1")
                .param("name", "catal()og")
                .flashAttr("catalogDto", new CatalogDto()))
                .andExpect(status().isOk())
                .andExpect(view().name("catalogs"))
                .andExpect(result -> containsString("<title>All catalogs</title>"));
    }

    @Test
    void processDeleteCatalog() throws Exception {
        userService.add(testData.getUserDto());
        catalogService.add(testData.getCatalogDto());

        mockMvc.perform(get("/catalog/delete?catalogName=catalog"))
                .andExpect(status().is(302))
                .andExpect(view().name("redirect:/catalog"))
                .andExpect(result -> containsString("<title>Catalog"));
    }

    private static class TestData {

        private UserDto getUserDto() {
            return new UserDto(1L, "login", "password");
        }

        private CatalogDto getCatalogDto() {
            return new CatalogDto(1L, "catalog", getUserDto());
        }
    }
}