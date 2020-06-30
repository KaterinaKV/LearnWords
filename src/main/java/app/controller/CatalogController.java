package app.controller;

import app.dto.CardDto;
import app.dto.CatalogDto;
import app.dto.UserDto;
import app.exception.InvalidInputDataException;
import app.service.CardService;
import app.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/catalog")
public class CatalogController {

    private final CatalogService catalogService;
    private final CardService cardService;

    @Autowired
    public CatalogController(CatalogService catalogService, CardService cardService) {
        this.catalogService = catalogService;
        this.cardService = cardService;
    }

    @GetMapping
    public String getCatalogPage(Model model, @AuthenticationPrincipal UserDto userDto) {
        model.addAttribute("catalogDto", new CatalogDto());
        model.addAttribute("catalogList", catalogService.findAllByUser(userDto));
        return "catalogs";
    }

    @PostMapping("/add")
    public String processAddCatalog(Model model, @AuthenticationPrincipal UserDto userDto,
                                    @ModelAttribute("catalogDto") CatalogDto catalogDto) {
        try {
            catalogDto.setUserDto(userDto);
            catalogService.add(catalogDto);
            return "redirect:/catalog";
        } catch (InvalidInputDataException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("catalogList", catalogService.findAllByUser(userDto));
            return "catalogs";
        }
    }

    @GetMapping("/show")
    public String getShowCatalogPage(Model model, @AuthenticationPrincipal UserDto userDto,
                                     @RequestParam("catalogName") String catalogName) {
        model.addAttribute("cardDto", new CardDto());
        model.addAttribute("catalogName", catalogName);
        model.addAttribute("cardList", cardService.findAllByCatalog(catalogService.findByNameAndUser(catalogName, userDto)));
        return "catalog";
    }

    @GetMapping("/update")
    public String getUpdateCatalogPage(Model model, @AuthenticationPrincipal UserDto userDto,
                                       @RequestParam("catalogName") String catalogName) {
        model.addAttribute("catalogList", catalogService.findAllByUser(userDto));
        model.addAttribute("catalogDto", catalogService.findByNameAndUser(catalogName, userDto));
        return "catalogs";
    }

    @PostMapping("/update")
    public String processUpdateCatalog(Model model, @AuthenticationPrincipal UserDto userDto,
                                       @ModelAttribute("catalogDto") CatalogDto catalogDto) {
        try {
            catalogDto.setUserDto(userDto);
            catalogService.update(catalogDto);
            return "redirect:/catalog"; /*/show?catalogName=" + catalogDto.getName();*/
        } catch (InvalidInputDataException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("catalogList", catalogService.findAllByUser(userDto));
            return "catalogs";
        }
    }

    @GetMapping("/delete")
    public String processDeleteCatalog(@RequestParam("catalogName") String catalogName,
                                       @AuthenticationPrincipal UserDto userDto) {
        catalogService.delete(catalogService.findByNameAndUser(catalogName, userDto));
        return "redirect:/catalog";
    }

}
