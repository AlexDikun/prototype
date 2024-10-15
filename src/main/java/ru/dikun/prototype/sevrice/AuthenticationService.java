package ru.dikun.prototype.sevrice;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import ru.dikun.prototype.controllers.dto.LoginUserDto;
import ru.dikun.prototype.domain.UserEntity;
import ru.dikun.prototype.repos.UserRepo;

@Service
public class AuthenticationService {

    private final UserRepo userRepo;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
        UserRepo userRepo, 
        AuthenticationManager authenticationManager
        ) {
            this.authenticationManager = authenticationManager;
            this.userRepo = userRepo;
    }

    public UserEntity authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                input.getLogin(), 
                input.getPassword()
            )
        );

        return userRepo.findByLogin(input.getLogin()).get();
    }  
}
