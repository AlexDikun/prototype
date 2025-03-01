package ru.dikun.prototype.services;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.validation.Validator;
import ru.dikun.prototype.domain.UserEntity;
import ru.dikun.prototype.repos.UserRepo;

@SpringBootTest
public class UserServiceTests {

    @Autowired
    UserRepo userRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    Validator validator;

    @Test
    void testUniqueLoginConstraint() {
        System.out.println("Попытка создать пользователя с существующим логином!");

        // Создаем первого пользователя и сохраняем его в базе данных
        UserEntity user1 = new UserEntity();
        user1.setLogin("uniqueUser");
        user1.setPassword(passwordEncoder.encode("secret"));
        userRepo.save(user1);

        // Создаем второго пользователя с тем же логином
        UserEntity user2 = new UserEntity();
        user2.setLogin("uniqueUser");
        user2.setPassword(passwordEncoder.encode("secret"));

        // Проверяем, что сохранение второго пользователя вызывает исключение
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepo.save(user2);
        });
    }
}
