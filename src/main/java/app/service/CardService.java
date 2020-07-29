package app.service;

import app.dto.CardDto;
import app.dto.CatalogDto;
import app.model.Card;

import java.util.List;

public interface CardService {

    CardDto add(CardDto cardDto);

    CardDto findById(long id);

    void update(CardDto cardDto);

    void delete(long id);

    List<CardDto> findAll();

    List<CardDto> findAllByCatalog(CatalogDto catalogDto);

    CardDto convertToDto(Card card);

    Card convertToEntity(CardDto cardDto);

}
