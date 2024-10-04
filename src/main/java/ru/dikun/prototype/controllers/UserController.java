package ru.dikun.prototype.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import ru.dikun.prototype.controllers.dto.UserDto;
import ru.dikun.prototype.domain.RoleEntity;
import ru.dikun.prototype.domain.UserEntity;
import ru.dikun.prototype.repos.RoleRepo;
import ru.dikun.prototype.repos.UserRepo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;


@Controller
public class UserController {

    @Autowired
    UserRepo userRepo;

    @Autowired
    RoleRepo roleRepo;

    @Autowired
    PasswordEncoder passwordEncoder;


    @GetMapping("/users/{id}")
    public ResponseEntity<UserEntity> getById(@PathVariable Long id) {
        System.out.println("Просмотр конкретного пользователя");

        Optional<UserEntity> optUserEntity = userRepo.findById(id);
        if (optUserEntity.isPresent())
            return new ResponseEntity<>(optUserEntity.get(), HttpStatus.OK);
        else 
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/users")
    public ResponseEntity<UserDto> postUsers(@RequestBody UserDto dto) {
        System.out.println("Создание аккаунта пользователя");

        UserEntity userEntity = new UserEntity();
        userEntity.setLogin(dto.getLogin());
        userEntity.setPassword(passwordEncoder.encode(dto.getPassword()));
        userEntity.setRoles(Collections.singletonList(roleRepo.findByName("ROLE_STAFF").get()));
        userRepo.save(userEntity);

        dto.setId(userEntity.getId());
        return new ResponseEntity<>(dto, HttpStatus.CREATED); 
    }

    @PatchMapping("roles/{id}/users/{id}")
    public ResponseEntity<String> updatingUserRole(@PathVariable Long role_id, @PathVariable Long id) {
        System.out.println("Администратор назначает пользователю новую роль!");

        Optional<UserEntity> user = userRepo.findById(id);
        Optional<RoleEntity> newRole = roleRepo.findById(role_id);
        
        if (user.isEmpty())
            return new ResponseEntity<>("Пользователь не найден", HttpStatus.NOT_FOUND);
        else if (newRole.isEmpty())
            return new ResponseEntity<>("Роль не найдена", HttpStatus.NOT_FOUND);
        else {
            user.get().setRoles(new ArrayList<>(Arrays.asList(newRole.get())));
            userRepo.save(user.get());
            return new ResponseEntity<>("Роль пользователя обновлена", HttpStatus.OK);
        }
            
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<String> changeUserPassword(@PathVariable Long id, @RequestBody UserDto dto) {
        System.out.println("Администратор назначает пользователю новый пароль!");

        Optional<UserEntity> userEntity = userRepo.findById(id);
        
        if (userEntity.isEmpty())
            return new ResponseEntity<>("Пользователь не найден", HttpStatus.NOT_FOUND);
        else {
            userEntity.get().setPassword(passwordEncoder.encode(dto.getPassword()));
            userRepo.save(userEntity.get());
            return new ResponseEntity<>("Пароль у учетной записи обновлен!", HttpStatus.OK);
        }

    }

    @DeleteMapping("/users")
    public ResponseEntity<String> deleteUser(@RequestBody UserDto dto) {
        System.out.println("Администратор удаляет ТОЛЬКО ПО ЛОГИНУ учетную запись пользователя из приложения!");

        Optional<UserEntity> userEntity = userRepo.findByLogin(dto.getLogin());
        if (userEntity.isEmpty())
            return new ResponseEntity<>("Пользователь не найден", HttpStatus.NOT_FOUND);
        else {
            userRepo.delete(userEntity.get());
            return new ResponseEntity<>("Пользователь удалён", HttpStatus.NO_CONTENT);
        }
    }
}
