package ru.dikun.prototype.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

import ru.dikun.prototype.controllers.dto.UserDto;
import ru.dikun.prototype.domain.UserEntity;
import ru.dikun.prototype.repos.RoleRepo;
import ru.dikun.prototype.repos.UserRepo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class UserController {

    @Autowired
    UserRepo userRepo;

    @Autowired
    RoleRepo roleRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/users")
    public ResponseEntity<UserDto> postUsers(@RequestBody UserDto dto) {
        System.out.println("Создание аккаунта пользователя");

        UserEntity userEntity = new UserEntity();
        userEntity.setLogin(dto.getLogin());
        userEntity.setPassword(passwordEncoder.encode(dto.getPassword()));
        userEntity.setRoles(Collections.singletonList(roleRepo.findByName("ROLE_STUFF").get()));
        userRepo.save(userEntity);

        dto.setId(userEntity.getId());
        return new ResponseEntity<>(dto, HttpStatus.CREATED); 
    }

    @GetMapping("/user")
    public ResponseEntity<String> userSpeach() {
        System.out.println("Проверка ролевых полномочий");

        return new ResponseEntity<>("Я есть пользователь", HttpStatus.OK);
    }
    
    
    
}
