package app.service.impl;

import app.dao.UserRepository;
import app.dto.UserDto;
import app.exception.InvalidInputDataException;
import app.model.User;
import app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImplUserService implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public ImplUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto add(UserDto userDto) {
        checkValid(userDto);
        User addedUser = userRepository.save(convertToEntity(userDto));
        return convertToDto(addedUser);
    }

    @Override
    public UserDto findByUsername(String username) {
        return convertToDto(userRepository.findByUsername(username));
    }

    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public UserDto convertToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getPassword()
        );
    }

    public User convertToEntity(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getUsername(),
                userDto.getPassword()
        );
    }

    private void checkValid(UserDto userDto) {
        if (userDto.getUsername().trim().isEmpty()) {
            throw new InvalidInputDataException("Login is required.");
        } else if (userDto.getPassword().trim().isEmpty()) {
            throw new InvalidInputDataException("Password is required.");
        } else if (!userDto.getUsername().matches("[a-zA-Z]{1,30}")) {
            throw new InvalidInputDataException("Login contains unauthorized symbols.");
        }
        for (UserDto user : findAll()) {
            if (userDto.getUsername().equals(user.getUsername())) {
                throw new InvalidInputDataException("User with this login already exists.");
            }
        }
    }
}
