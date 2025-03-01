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

import ru.dikun.prototype.domain.RoleEntity;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RoleRepoTests {

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private TestEntityManager entityManager;

    private RoleEntity role;

    @BeforeEach
    public void setUp() {
        role = new RoleEntity();
        role.setName("ROLE_LOCK");
        entityManager.persist(role);
    }

    @Test
    void testFindByNameSuccessfully() {
        Optional<RoleEntity> foundRole = roleRepo.findByName("ROLE_LOCK");
        assertTrue(foundRole.isPresent());
        assertEquals("ROLE_LOCK", foundRole.get().getName());
    }

    @Test
    void testFindByNameNotFound() {
        Optional<RoleEntity> foundRole = roleRepo.findByName("ROLE_NON_EXISTENT");
        assertTrue(foundRole.isEmpty());
    }
    
}
