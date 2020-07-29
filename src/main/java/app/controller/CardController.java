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

import java.util.ArrayList;

@Controller
@RequestMapping("/catalog/card")
public class CardController {

    private final CardService cardService;
    private final CatalogService catalogService;

    @Autowired
    public CardController(CardService cardService, CatalogService catalogService) {
        this.cardService = cardService;
        this.catalogService = catalogService;
    }

    @GetMapping("/add")
    public String getCatalogPage(Model model, @RequestParam("catalogName") String catalogName) {
        model.addAttribute("cardList", new ArrayList<CardDto>());
        model.addAttribute("catalogName", catalogName);
        model.addAttribute("cardDto", new CardDto());
        return "catalog";
    }

    @PostMapping("/add")
    public String processAddCard(Model model, @AuthenticationPrincipal UserDto userDto,
                                 @RequestParam("catalogName") String catalogName,
                                 @ModelAttribute("cardDto") CardDto cardDto) {
        CatalogDto catalogDto = catalogService.findByNameAndUser(catalogName, userDto);
        try {
            cardDto.setCatalogDto(catalogDto);
            cardService.add(cardDto);
            return "redirect:/catalog/show?catalogName=" + catalogName;
        } catch (InvalidInputDataException ex) {
            model.addAttribute("cardList", cardService.findAllByCatalog(catalogDto));
            model.addAttribute("catalogName", catalogName);
            model.addAttribute("error", ex.getMessage());
            return "catalog";
        }
    }

    @GetMapping("/update")
    public String getUpdateCardForm(Model model, @RequestParam("id") long id) {
        CardDto cardToUpdate = cardService.findById(id);
        CatalogDto catalogDto = cardToUpdate.getCatalogDto();
        model.addAttribute("cardList", cardService.findAllByCatalog(catalogDto));
        model.addAttribute("cardDto", cardToUpdate);
        model.addAttribute("catalogName", catalogDto.getName());
        return "catalog";
    }

    @PostMapping("/update")
    public String processUpdateCard(Model model, @AuthenticationPrincipal UserDto userDto,
                                    @ModelAttribute("cardDto") CardDto cardDto,
                                    @RequestParam("catalogName") String catalogName) {
        CatalogDto catalogDto = catalogService.findByNameAndUser(catalogName, userDto);
        model.addAttribute("catalogName", catalogName);
        try {
            cardDto.setCatalogDto(catalogDto);
            cardService.update(cardDto);
            return "redirect:/catalog/show?catalogName=" + cardDto.getCatalogDto().getName();
        } catch (InvalidInputDataException ex) {
            model.addAttribute("cardList", cardService.findAllByCatalog(catalogDto));
            model.addAttribute("error", ex.getMessage());
            return "catalog";
        }
    }

    @GetMapping("/delete")
    public String processDeleteCard(@RequestParam("id") long id) {
        String catalogName = cardService.findById(id).getCatalogDto().getName();
        cardService.delete(id);
        return "redirect:/catalog/show?catalogName=" + catalogName;
    }
}
