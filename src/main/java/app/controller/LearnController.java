package app.controller;

import app.dto.CardDto;
import app.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/learn")
public class LearnController {

    private final CardService cardService;

    @Autowired
    public LearnController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping("/card")
    public String showCards(Model model, @RequestParam(value = "id", required = false) Long id,
                            @RequestParam(value = "isWord", required = false) Boolean isWord) {
        if (id == null) {
            model.addAttribute("isWord", true);
            model.addAttribute("card", cardService.findAll().get(0));
        } else {
            model.addAttribute("isWord", isWord);
            CardDto cardDto = cardService.findById(id);
            if (cardDto==null){
                model.addAttribute("message", "Chosen list words completed");
                return "learnCard";
            }
            model.addAttribute("card", cardService.findById(id));
        }
        return "learnCard";
    }

   /* @GetMapping("/translate")
    public String showTranslationCards(Model model, @RequestParam("id") Long id) {
       CardDto cardDto = new CardDto(1L, "smth", "что-то","");
        model.addAttribute("word", false);
        model.addAttribute("card", cardDto);
        return "redirect:/learn?id=" + id + "&isWord=" + false;
    }

    @GetMapping("/next")
    public String showNextCards(Model model, @RequestParam("id") Long id) {
        return "redirect:/learn?id=" + (id + 1) + "&isWord=" + true;
    }*/

}
