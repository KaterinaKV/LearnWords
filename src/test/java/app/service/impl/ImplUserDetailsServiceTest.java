package app.service.impl;

import app.dto.UserDto;
import app.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ImplUserDetailsServiceTest {

    @TestConfiguration
    static class TestContextConfiguration {
        @Bean
        public UserDetailsService userDetailsService() {
            return new ImplUserDetailsService();
        }
    }

    @Autowired
    private UserDetailsService userDetailsService;

    @MockBean
    private UserService mockUserService;

    @Test
    void loadUserByUsernameSuccess() {
        UserDto userDto = new UserDto(1L, "login", "password");
        when(mockUserService.findByUsername("login")).thenReturn(userDto);
        UserDetails foundUserDto = userDetailsService.loadUserByUsername("login");

        assertNotNull(foundUserDto);
        verify(mockUserService, times(1)).findByUsername("login");
    }

    @Test
    void loadUserByUsernameFailed() {
        when(mockUserService.findByUsername("NotExist")).thenReturn(null);

        Throwable thrown = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("NotExist"));
        assertNotNull(thrown.getMessage());
        assertEquals("User NotExist not found.", thrown.getMessage());
        verify(mockUserService, times(1)).findByUsername("NotExist");
    }

}