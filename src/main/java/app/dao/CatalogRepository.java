package app.dao;

import app.model.Catalog;
import app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CatalogRepository extends JpaRepository<Catalog, Long> {

    Catalog findByNameAndUser(String name, User user);

    List<Catalog> findAllByUser(User user);
}
