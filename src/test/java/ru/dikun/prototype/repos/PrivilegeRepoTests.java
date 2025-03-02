package ru.dikun.prototype.repos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ru.dikun.prototype.domain.PrivilegeEntity;

public class PrivilegeRepoTests {

    @Mock
    private PrivilegeRepo privilegeRepo;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByNameSuccessfully() {
        String privilegeName = "READ_PRIVILEGE";
        PrivilegeEntity privilege = new PrivilegeEntity();
        privilege.setName(privilegeName);

        when(privilegeRepo.findByName(privilegeName)).thenReturn(Optional.of(privilege));

        Optional<PrivilegeEntity> foundPrivilege = privilegeRepo.findByName(privilegeName);

        assertTrue(foundPrivilege.isPresent());
        assertEquals(privilegeName, foundPrivilege.get().getName());
    }

    @Test
    void testFindByNameNotFound() {
        String privilegeName = "NON_EXISTENT_PRIVILEGE";
        when(privilegeRepo.findByName(privilegeName)).thenReturn(Optional.empty());

        Optional<PrivilegeEntity> foundPrivilege = privilegeRepo.findByName(privilegeName);

        assertTrue(foundPrivilege.isEmpty());
    }
    
}
