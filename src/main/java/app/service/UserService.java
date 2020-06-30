package app.service;

import app.dto.UserDto;
import app.model.User;

public interface UserService {

    UserDto add(UserDto userDto);

    UserDto findByUsername(String username);

    UserDto convertToDto(User user);

    User convertToEntity(UserDto userDto);
}
