package app.service.impl;

import app.dao.UserRepository;
import app.dto.UserDto;
import app.exception.InvalidInputDataException;
import app.model.User;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImplUserServiceTest {

    @Mock
    private UserRepository mockUserRepository;
    @InjectMocks
    private ImplUserService userService;
    private final TestData testData = new TestData();

    @Nested
    class AddUser {
        @Test
        void addUser_Success() {
            User userToAdd = testData.getUser();
            when(mockUserRepository.save(userToAdd)).thenReturn(userToAdd);

            UserDto addedUserDto = userService.add(testData.getUserDto());

            assertNotNull(addedUserDto);
            isUsersEquals(addedUserDto, userToAdd);
            verify(mockUserRepository, times(1)).save(userToAdd);
        }

        @Test
        void addUser_Failed_UserAlreadyExist() {
            when(mockUserRepository.findAll()).thenReturn(testData.getListUsers());

            Throwable thrown = assertThrows(InvalidInputDataException.class, () -> userService.add(testData.getUserDto()));
            assertNotNull(thrown.getMessage());
            assertEquals("User with this login already exists.", thrown.getMessage());
            verify(mockUserRepository, times(1)).findAll();
        }

        @Test
        void addUser_Failed_LoginIsEmpty() {
            UserDto userDtoWithEmptyLogin = testData.getUserDtoWithEmptyLogin();

            Throwable thrown = assertThrows(InvalidInputDataException.class, () -> userService.add(userDtoWithEmptyLogin));
            assertNotNull(thrown.getMessage());
            assertEquals("Login is required.", thrown.getMessage());
        }

        @Test
        void addUser_Failed_PasswordIsEmpty() {
            UserDto userDtoWithEmptyPassword = testData.getUserDtoWithEmptyPassword();

            Throwable thrown = assertThrows(InvalidInputDataException.class, () -> userService.add(userDtoWithEmptyPassword));
            assertNotNull(thrown.getMessage());
            assertEquals("Password is required.", thrown.getMessage());
        }

        @Test
        void addUser_Failed_LoginContainsUnauthorizedSymbols() {
            UserDto userDtoWithIncorrectLogin = testData.getUserDtoWithIncorrectLogin();

            Throwable thrown = assertThrows(InvalidInputDataException.class, () -> userService.add(userDtoWithIncorrectLogin));
            assertNotNull(thrown.getMessage());
            assertEquals("Login contains unauthorized symbols.", thrown.getMessage());
        }

        @Test
        void addUser_Failed_LoginIsTooLong() {
            UserDto userDtoWithLongLogin = testData.getUserDtoWithLongLogin();

            Throwable thrown = assertThrows(InvalidInputDataException.class, () -> userService.add(userDtoWithLongLogin));
            assertNotNull(thrown.getMessage());
            assertEquals("Login contains unauthorized symbols.", thrown.getMessage());
        }
    }

    @Nested
    class FindUser {
        @Test
        void findUserByUsername_Success() {
            User userToFind = testData.getUser();
            when(mockUserRepository.findByUsername("login")).thenReturn(userToFind);

            UserDto foundUserDto = userService.findByUsername("login");

            assertNotNull(foundUserDto);
            isUsersEquals(foundUserDto, userToFind);
            verify(mockUserRepository).findByUsername("login");
        }

        @Test
        void findUserByUsername_Failed() {
            when(mockUserRepository.findByUsername("NotExist")).thenReturn(null);

            UserDto foundUserDto = userService.findByUsername("NotExist");

            assertNull(foundUserDto);
            verify(mockUserRepository).findByUsername("NotExist");
        }
    }

    @Nested
    class FindAllUsers {
        @Test
        void findAllUsers_Success() {
            List<User> users = testData.getListUsers();
            when(mockUserRepository.findAll()).thenReturn(users);

            List<UserDto> usersDto = userService.findAll();

            assertNotEquals(0, usersDto.size());
            isUsersListEquals(usersDto, users);
            verify(mockUserRepository, times(1)).findAll();
        }

        @Test
        void findAllUsers_Failed() {
            when(mockUserRepository.findAll()).thenReturn(new ArrayList<>());

            assertEquals(userService.findAll().size(), 0);
            verify(mockUserRepository, times(1)).findAll();
        }
    }

    @Nested
    class Convert {

        @Test
        void convertToDto() {
            User userToConvert = testData.getUser();
            UserDto convertedUserDto = userService.convertToDto(userToConvert);
            isUsersEquals(convertedUserDto, userToConvert);
        }

        @Test
        void convertToEntity() {
            UserDto userDtoToConvert = testData.getUserDto();
            User convertedUser = userService.convertToEntity(userDtoToConvert);
            isUsersEquals(userDtoToConvert, convertedUser);
        }

    }

    static void isUsersEquals(UserDto userDto, User user) {
        assertEquals(userDto.getId(), user.getId(), "Ids do not match");
        assertEquals(userDto.getUsername(), user.getUsername(), "Usernames do not match");
        assertEquals(userDto.getPassword(), user.getPassword(), "Passwords do not match");
    }

    private void isUsersListEquals(List<UserDto> usersDto, List<User> users) {
        assertEquals(usersDto.size(), users.size());
        for (int i = 0; i < users.size(); i++) {
            isUsersEquals(usersDto.get(i), users.get(i));
        }
    }

    private static class TestData {
        private User getUser() {
            return new User(1L, "login", "password");
        }

        private UserDto getUserDto() {
            return new UserDto(1L, "login", "password");
        }

        private UserDto getUserDtoWithEmptyLogin() {
            return new UserDto(1L, "", "password");
        }

        private UserDto getUserDtoWithEmptyPassword() {
            return new UserDto(1L, "login", "");
        }

        private UserDto getUserDtoWithIncorrectLogin() {
            return new UserDto(1L, "logi56{}n", "password");
        }

        private UserDto getUserDtoWithLongLogin() {
            return new UserDto(1L, "loginnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn", "password");
        }

        private List<User> getListUsers() {
            List<User> users = new ArrayList<>();
            users.add(getUser());
            return users;
        }
    }
}