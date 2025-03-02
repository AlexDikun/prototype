package ru.dikun.prototype.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import ru.dikun.prototype.domain.PrivilegeEntity;
import ru.dikun.prototype.domain.RoleEntity;
import ru.dikun.prototype.domain.UserEntity;
import ru.dikun.prototype.repos.RoleRepo;
import ru.dikun.prototype.repos.UserRepo;
import ru.dikun.prototype.sevrice.UserService;

@SpringBootTest
public class UserServiceTests {

    @Mock
    UserRepo userRepo;

    @Mock
    RoleRepo roleRepo;

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

    @Test
    void testLoadUserByUsernameSuccessfully() {
        System.out.println("Успешная попытка загрузить пользователя из БД");

        String login = "testUser";
  
        UserEntity user = new UserEntity();
        user.setLogin(login);
        user.setPassword("secret");
        RoleEntity role = new RoleEntity();
        role.setName("ROLE_STAFF");
        user.setRoles(Collections.singletonList(role));

        when(userRepo.findByLogin(login)).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername(login);

        assertNotNull(userDetails);
        assertEquals(login, userDetails.getUsername());
        assertEquals("secret", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STAFF")));
    }

    @Test
    void testLoadUserByUsernameNotFound() {
        System.out.println("Неуспешная попытка загрузить пользователя из БД");

        String login = "nonExistentUser";
        when(userRepo.findByLogin(login)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername(login);
        });
    }

    @Test
    void testGetGrantedAuthorities() {
        // Arrange
        List<String> privileges = Arrays.asList("READ_PRIVILEGE", "USER_WRITE_PRIVILEGE");

        // Act
        List<GrantedAuthority> authorities = userService.getGrantedAuthoritiesPublic(privileges);

        // Assert
        assertEquals(2, authorities.size());
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("USER_WRITE_PRIVILEGE")));
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("READ_PRIVILEGE")));
    }

    @Test
    void testGetPrivileges() {
        // Arrange
        RoleEntity role = new RoleEntity();
        role.setName("ROLE_STAFF");
        PrivilegeEntity privilege = new PrivilegeEntity();
        privilege.setName("READ_PRIVILEGE");
        role.setPrivileges(Collections.singletonList(privilege));
        Collection<RoleEntity> roles = Collections.singletonList(role);

        // Act
        List<String> privileges = userService.getPrivilegesPublic(roles);

        // Assert
        assertEquals(2, privileges.size());
        assertTrue(privileges.contains("ROLE_STAFF"));
        assertTrue(privileges.contains("READ_PRIVILEGE"));
    }

    @Test
    void testGetAuthorities() {
        // Arrange
        RoleEntity role = new RoleEntity();
        role.setName("ROLE_STAFF");
        PrivilegeEntity privilege = new PrivilegeEntity();
        privilege.setName("READ_PRIVILEGE");
        role.setPrivileges(Collections.singletonList(privilege));
        Collection<RoleEntity> roles = Collections.singletonList(role);

        // Act
        Collection<? extends GrantedAuthority> authorities = userService.getAuthoritiesPublic(roles);

        // Assert
        assertEquals(2, authorities.size());
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_STAFF")));
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("READ_PRIVILEGE")));
    }
}
