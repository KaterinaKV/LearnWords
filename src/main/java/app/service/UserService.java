package app.service;

import app.dto.UserDto;
import app.model.Catalog;
import app.model.User;

import java.util.List;

public interface UserService {

    UserDto add(UserDto userDto);

    //UserDto findById(long id);

    UserDto findByUsername(String username);

    UserDto convertToDto(User user);

    User convertToEntity(UserDto userDto);
}
