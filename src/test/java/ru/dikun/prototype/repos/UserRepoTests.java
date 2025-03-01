package ru.dikun.prototype.repos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import ru.dikun.prototype.domain.UserEntity;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
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
