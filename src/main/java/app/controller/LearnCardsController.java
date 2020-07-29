package app.controller;

import app.dto.CardDto;
import app.dto.CatalogDto;
import app.dto.UserDto;
import app.service.CardService;
import app.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Controller
@RequestMapping
public class LearnCardsController {

    private final CatalogService catalogService;
    private final CardService cardService;

    @Autowired
    public LearnCardsController(CatalogService catalogService, CardService cardService) {
        this.catalogService = catalogService;
        this.cardService = cardService;
    }

    @GetMapping("/catalog/learn")
    public String processLearnCards(Model model, @AuthenticationPrincipal UserDto userDto,
                                    @RequestParam(value = "catalogName") String catalogName,
                                    @RequestParam(value = "i", required = false) Integer i,
                                    @RequestParam(value = "isWord", required = false) Boolean isWord,
                                    @RequestParam(value = "order", required = false) Long order) {
        model.addAttribute("catalogName", catalogName);
        CatalogDto catalogDto = catalogService.findByNameAndUser(catalogName, userDto);
        List<CardDto> cardList = cardService.findAllByCatalog(catalogDto);
        if (order == null) {
            order = new Date().getTime();
        }
        Collections.shuffle(cardList, new Random(order));

        if (i == null) {
            model.addAttribute("isWord", true);
            model.addAttribute("card", cardList.get(0));
            model.addAttribute("i", 0);
        } else {
            model.addAttribute("isWord", isWord);
            if (cardList.size() <= i) {
                model.addAttribute("error", "Catalog cards are finished");
            } else {
                model.addAttribute("card", cardList.get(i));
                model.addAttribute("i", i);
            }
        }
        model.addAttribute("order", order);
        return "learnCard";
    }
}
