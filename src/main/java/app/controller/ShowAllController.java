package app.controller;

import app.dao.CardRepository;
import app.dto.CardDto;
import app.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping
public class ShowAllController {

    private final CardService cardService;

    @Autowired
    public ShowAllController(CardService cardService){
        this.cardService = cardService;
    }

    @GetMapping("/showAll")
    public String showWordsPage(Model model){
        List<CardDto> cards = cardService.findAll();
        System.out.println("size cards"+cards.size());
        model.addAttribute("cards", cards);
        return "showAll";
    }

}
