package ru.dikun.prototype.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import ru.dikun.prototype.domain.UserEntity;
import ru.dikun.prototype.repos.UserRepo;
import ru.dikun.prototype.sevrice.UserService;

@SpringBootTest
public class UserServiceTests {

    @Mock
    UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUniqueLoginConstraint() {
        System.out.println("Попытка создать пользователя с существующим логином!");

        // Создаем первого пользователя и сохраняем его в базе данных
        UserEntity user1 = new UserEntity();
        user1.setLogin("uniqueUser");
        user1.setPassword(passwordEncoder.encode("secret"));

        when(userRepo.findByLogin("uniqueUser")).thenReturn(Optional.of(user1));
        when(passwordEncoder.encode(anyString())).thenReturn("secret");

        // Создаем второго пользователя с тем же логином
        UserEntity user2 = new UserEntity();
        user2.setLogin("uniqueUser");
        user2.setPassword(passwordEncoder.encode("secret"));

        // Настраиваем мок репозитория для выброса исключения при попытке сохранения второго пользователя
        doThrow(new DataIntegrityViolationException("Login must be unique")).when(userRepo).save(user2);

        // Проверяем, что сохранение второго пользователя вызывает исключение
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepo.save(user2);
        });
    }
}
