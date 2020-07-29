package app.service.impl;

import app.dao.CatalogRepository;
import app.dto.CatalogDto;
import app.dto.UserDto;
import app.exception.InvalidInputDataException;
import app.model.Catalog;
import app.model.User;
import app.service.CatalogService;
import app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImplCatalogService implements CatalogService {

    private final CatalogRepository catalogRepository;
    private final UserService userService;

    @Autowired
    public ImplCatalogService(CatalogRepository catalogRepository, UserService userService) {
        this.catalogRepository = catalogRepository;
        this.userService = userService;
    }

    @Override
    public CatalogDto add(CatalogDto catalogDto) {
        checkValid(catalogDto);
        Catalog catalog = catalogRepository.save(convertToEntity(catalogDto));
        return convertToDto(catalog);
    }

    @Override
    public CatalogDto findByNameAndUser(String name, UserDto userDto) {
        User user = userService.convertToEntity(userDto);
        Catalog catalog = catalogRepository.findByNameAndUser(name, user);
        return convertToDto(catalog);
    }

    public CatalogDto findById(long id) {
        Catalog catalog = catalogRepository.findById(id).orElse(null);
        if (catalog == null) {
            return null;
        }
        return convertToDto(catalog);
    }

    @Override
    public void update(CatalogDto catalogDto) {
        checkValid(catalogDto);
        CatalogDto catalogToUpdate = findById(catalogDto.getId());
        if (catalogToUpdate == null) {
            throw new EntityNotFoundException("Catalog for updating not found.");
        }
        catalogToUpdate.setName(catalogDto.getName());
        catalogRepository.save(convertToEntity(catalogToUpdate));
    }

    @Override
    public void delete(CatalogDto catalogDto) {
        catalogRepository.deleteById(catalogDto.getId());
    }

    @Override
    public List<CatalogDto> findAllByUser(UserDto userDto) {
        return catalogRepository.findAllByUser(userService.convertToEntity(userDto)).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public CatalogDto convertToDto(Catalog catalog) {
        int countCard;
        if (catalog.getCardList() == null || catalog.getCardList().isEmpty()) {
            countCard = 0;
        } else {
            countCard = catalog.getCardList().size();
        }
        return new CatalogDto(
                catalog.getId(),
                catalog.getName(),
                countCard,
                userService.convertToDto(catalog.getUser())
        );
    }

    public Catalog convertToEntity(CatalogDto catalogDto) {
        return new Catalog(
                catalogDto.getId(),
                catalogDto.getName(),
                userService.convertToEntity(catalogDto.getUserDto())
        );
    }

    private void checkValid(CatalogDto catalogDto) {
        if (catalogDto.getName().trim().isEmpty()) {
            throw new InvalidInputDataException("Name catalog is required.");
        } else if (!catalogDto.getName().matches("[a-zA-Zа-яА-ЯёЁ0-9\\s]{1,100}")) {
            throw new InvalidInputDataException("Name catalog contains unauthorized symbols.");
        }
        for (CatalogDto catalog : findAllByUser(catalogDto.getUserDto())) {
            if (catalogDto.getName().equals(catalog.getName())) {
                throw new InvalidInputDataException("Such catalog already exists.");
            }
        }
    }
}
