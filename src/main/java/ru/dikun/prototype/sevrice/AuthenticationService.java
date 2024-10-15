package ru.dikun.prototype.sevrice;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import ru.dikun.prototype.controllers.dto.LoginUserDto;

@Service
public class AuthenticationService {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
        UserService userService, 
        AuthenticationManager authenticationManager
        ) {
            this.authenticationManager = authenticationManager;
            this.userService = userService;
    }

    public UserDetails authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                input.getLogin(), 
                input.getPassword()
            )
        );

        return userService.loadUserByUsername(input.getLogin());
    }  
}
