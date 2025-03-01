package ru.dikun.prototype.repos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ru.dikun.prototype.domain.UserEntity;

public class UserRepoTests {

    @Mock
    private UserRepo userRepo;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByLoginSuccessfully() {
        String login = "testUser";
        UserEntity user = new UserEntity();
        user.setLogin(login);
        user.setPassword("secret");

        when(userRepo.findByLogin(login)).thenReturn(Optional.of(user));

        Optional<UserEntity> foundUser = userRepo.findByLogin(login);

        assertTrue(foundUser.isPresent());
        assertEquals(login, foundUser.get().getLogin());
        assertEquals("secret", foundUser.get().getPassword());
    }

    @Test
    void testFindByLoginNotFound() {
        String login = "nonExistentUser";
        when(userRepo.findByLogin(login)).thenReturn(Optional.empty());

        Optional<UserEntity> foundUser = userRepo.findByLogin(login);

        assertTrue(foundUser.isEmpty());
    }
    
}
