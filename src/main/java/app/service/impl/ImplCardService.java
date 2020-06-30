package app.service.impl;

import app.dao.CardRepository;
import app.dto.CardDto;
import app.dto.CatalogDto;
import app.exception.InvalidInputDataException;
import app.model.Card;
import app.service.CardService;
import app.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImplCardService implements CardService {

    private final CardRepository cardRepository;
    private final CatalogService catalogService;

    @Autowired
    public ImplCardService(CardRepository cardRepository, CatalogService catalogService) {
        this.cardRepository = cardRepository;
        this.catalogService = catalogService;
    }

    @Override
    public CardDto add(CardDto cardDto) {
        checkValid(cardDto);
        Card addedCard = cardRepository.save(convertToEntity(cardDto));
        return convertToDto(addedCard);
    }

    @Override
    public CardDto findById(long id) {
        Card foundCard = cardRepository.findById(id).orElse(null);
        if (foundCard == null) {
            return null;
        }
        return convertToDto(foundCard);
    }

    @Override
    public void update(CardDto cardDto) {
        CardDto cardToUpdate = findById(cardDto.getId());
        if (cardToUpdate == null) {
            throw new EntityNotFoundException("Card for updating not found.");
        }
        cardToUpdate.setWord(cardDto.getWord());
        cardToUpdate.setTranslation(cardDto.getTranslation());
        cardToUpdate.setTranscription(cardDto.getTranscription());

        checkValid(cardToUpdate);
        cardRepository.save(convertToEntity(cardToUpdate));
    }

    @Override
    public void delete(long id) {
        cardRepository.deleteById(id);
    }

    @Override
    public List<CardDto> findAll() {
        return cardRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CardDto> findAllByCatalog(CatalogDto catalogDto) {
        return cardRepository.findAllByCatalog(catalogService.convertToEntity(catalogDto)).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public CardDto convertToDto(Card card) {
        return new CardDto(
                card.getId(),
                card.getWord(),
                card.getTranslation(),
                card.getTranscription(),
                catalogService.convertToDto(card.getCatalog())
        );
    }

    public Card convertToEntity(CardDto cardDto) {
        return new Card(
                cardDto.getId(),
                cardDto.getWord(),
                cardDto.getTranslation(),
                cardDto.getTranscription(),
                catalogService.convertToEntity(cardDto.getCatalogDto())
        );
    }

    private void checkValid(CardDto cardDto) {
        if (cardDto.getWord().isEmpty()) {
            throw new InvalidInputDataException("Word is required.");
        } else if (cardDto.getTranslation().isEmpty()) {
            throw new InvalidInputDataException("Translation is required.");
        } else if (!cardDto.getWord().matches("[a-zA-Zа-яА-ЯёЁ\\s]{1,100}")) {
            throw new InvalidInputDataException("The \"Word\" field contains unauthorized symbols.");
        } else if (!cardDto.getTranslation().matches("[a-zA-Zа-яА-ЯёЁ\\s]{1,100}")) {
            throw new InvalidInputDataException("The \"Translation\" field contains unauthorized symbols.");
        } else if (hasEqualsCardWithoutId(cardDto)) {
            throw new InvalidInputDataException("Such card already exists.");
        }
    }

    private boolean hasEqualsCardWithoutId(CardDto cardDto) {
        for (CardDto card : findAllByCatalog(cardDto.getCatalogDto())) {
            if (card.getWord().equals(cardDto.getWord())
                    && card.getTranslation().equals(cardDto.getTranslation())
                    && card.getTranscription().equals(cardDto.getTranscription())) {
                return true;
            }
        }
        return false;
    }
}
