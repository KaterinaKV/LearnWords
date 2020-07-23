package app.service.impl;

import app.dto.UserDto;
import app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ImplUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserDto userDto = userService.findByUsername(username);
        if (userDto != null) {
            return userDto;
        }
        throw new UsernameNotFoundException("User " + username + " not found.");
    }
}
