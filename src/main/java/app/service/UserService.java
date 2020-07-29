package app.service;

import app.dto.UserDto;
import app.model.User;

import java.util.List;

public interface UserService {

    UserDto add(UserDto userDto);

    UserDto findByUsername(String username);

    List<UserDto> findAll();

    UserDto convertToDto(User user);

    User convertToEntity(UserDto userDto);

}
