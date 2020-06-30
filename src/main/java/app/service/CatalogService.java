package app.service;

import app.dto.CatalogDto;
import app.dto.UserDto;
import app.model.Catalog;

import java.util.List;

public interface CatalogService {

    CatalogDto add(CatalogDto catalogDto);

    CatalogDto findByNameAndUser(String name, UserDto user);

    void update(CatalogDto catalogDto);

    void delete(CatalogDto catalogDto);

    List<CatalogDto> findAllByUser(UserDto userDto);

    CatalogDto convertToDto(Catalog catalog);

    Catalog convertToEntity(CatalogDto catalogDto);
}
