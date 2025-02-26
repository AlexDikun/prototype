package ru.dikun.prototype.domain;

import static org.assertj.core.api.Assertions.assertThat;

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
        UserEntity user = new UserEntity();
        user.setLogin(""); // blank login
        user.setPassword(passwordEncoder.encode("secret"));

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().anyMatch(violation -> violation.getMessage().contains("must not be blank"))).isTrue();
    }

}
