package app.dao;

import app.model.Card;
import app.model.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findAllByCatalog(Catalog catalog);

}
