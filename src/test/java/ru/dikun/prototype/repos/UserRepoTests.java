package ru.dikun.prototype.repos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;

import ru.dikun.prototype.domain.UserEntity;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserRepoTests {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private TestEntityManager entityManager;

    private UserEntity userEntity;

    @BeforeEach
    public void setUp() {
        userEntity = new UserEntity();
        userEntity.setLogin("testUser");
        userEntity.setPassword("secret");
        entityManager.persist(userEntity);
    }

    @Test
    void testFindByLoginSuccessfully() {
        Optional<UserEntity> foundUser = userRepo.findByLogin("testUser");
        assertTrue(foundUser.isPresent());
        assertEquals("testUser", foundUser.get().getLogin());
        assertEquals("secret", foundUser.get().getPassword());
    }

    @Test
    void testFindByLoginNotFound() {
        Optional<UserEntity> foundUser = userRepo.findByLogin("nonExistentUser");
        assertTrue(foundUser.isEmpty());
    }
    
}
