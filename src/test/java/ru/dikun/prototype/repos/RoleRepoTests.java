package ru.dikun.prototype.repos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ru.dikun.prototype.domain.RoleEntity;

public class RoleRepoTests {

    @Mock
    private RoleRepo roleRepo;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByNameSuccessfully() {
        String roleName = "ROLE_STAFF";
        RoleEntity role = new RoleEntity();
        role.setName(roleName);

        when(roleRepo.findByName(roleName)).thenReturn(Optional.of(role));

        Optional<RoleEntity> foundRole = roleRepo.findByName(roleName);

        assertTrue(foundRole.isPresent());
        assertEquals(roleName, foundRole.get().getName());
    }

    @Test
    void testFindByNameNotFound() {
        String roleName = "ROLE_NON_EXISTENT";
        when(roleRepo.findByName(roleName)).thenReturn(Optional.empty());

        Optional<RoleEntity> foundRole = roleRepo.findByName(roleName);

        assertTrue(foundRole.isEmpty());
    }
    
}
