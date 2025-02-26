package ru.dikun.prototype.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import ru.dikun.prototype.repos.RoleRepo;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;


@SpringBootTest
public class UserEntityTests {
    private static Validator validator;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RoleRepo roleRepo;


    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }


    @Test
    void testUserValidation() {
        System.out.println("Создаем валидного пользователя!");

        UserEntity user = new UserEntity();
        user.setLogin("test@company.ru");
        user.setPassword(passwordEncoder.encode("secret"));

        Optional<RoleEntity> roleOptional = roleRepo.findByName("ROLE_STAFF");
        RoleEntity role = roleOptional.orElseThrow(() -> new RuntimeException("Role not found"));

        user.setRoles(Collections.singletonList(role));

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);
        assertThat(violations).isEmpty();
    }

    @Test
    void testUserValidationWithBlankLogin() {
        System.out.println("Попытка создать пользователя с пустым логином!");

        UserEntity user = new UserEntity();
        user.setLogin(""); // blank login
        user.setPassword(passwordEncoder.encode("secret"));

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().anyMatch(violation -> violation.getMessage().contains("must not be blank"))).isTrue();
    }

    @Test
    void testUserValidationWithBlankPassword() {
        System.out.println("Попытка создать пользователя с пустым паролем!");

        UserEntity user = new UserEntity();
        user.setLogin("bagowix@company.ru"); 
        user.setPassword(""); // blank password

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().anyMatch(violation -> violation.getMessage().contains("must not be blank"))).isTrue();
    }

    @Test
    void testUserValidationWithShortPassword() {
        System.out.println("Попытка создать пользователя с коротким паролем!");

        UserEntity user = new UserEntity();
        user.setLogin("bagowix@company.ru");
        user.setPassword("1");

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().anyMatch(violation -> violation.getMessage().contains("error"))).isTrue();
    }

}
