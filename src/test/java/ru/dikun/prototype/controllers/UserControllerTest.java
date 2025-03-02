package ru.dikun.prototype.controllers;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ru.dikun.prototype.PrototypeApplication;
import ru.dikun.prototype.controllers.dto.UserDto;
import ru.dikun.prototype.domain.UserEntity;
import ru.dikun.prototype.domain.RoleEntity;
import ru.dikun.prototype.repos.RoleRepo;
import ru.dikun.prototype.repos.UserRepo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = PrototypeApplication.class)
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude=SecurityAutoConfiguration.class)
@TestPropertySource(locations = "classpath:application.properties")
public class UserControllerTest {

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private RoleRepo roleRepo;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    UserDto userDto;
    
    private String managersLogin = "manager@company.ru";
    private String internsLogin = "intern@company.ru";


    @BeforeEach
    private void setup() {
        MockitoAnnotations.openMocks(this);

        UserEntity manager = new UserEntity();
        manager.setId(4L);
        manager.setLogin(managersLogin);
        manager.setPassword(passwordEncoder.encode("secret"));

        RoleEntity roleStaff = new RoleEntity();
        roleStaff.setId(4L);
        roleStaff.setName("ROLE_STAFF");
        manager.setRoles(Collections.singletonList(roleStaff));

        RoleEntity roleModer = new RoleEntity();
        roleModer.setId(5L);
        roleModer.setName("ROLE_MODER");

        reset(userRepo, roleRepo, passwordEncoder);

        when(userRepo.findByLogin(managersLogin)).thenReturn(Optional.of(manager));
        when(userRepo.findById(4L)).thenReturn(Optional.of(manager));

        when(roleRepo.findByName("ROLE_STAFF")).thenReturn(Optional.of(roleStaff));
        when(roleRepo.findById(4L)).thenReturn(Optional.of(roleStaff));
        when(roleRepo.findByName("ROLE_MODER")).thenReturn(Optional.of(roleModer));
        when(roleRepo.findById(5L)).thenReturn(Optional.of(roleModer));
    }

    // tests

    @Test
    @WithMockUser(roles="ADMIN")
    void checkManagerProfile() throws Exception {
        UserEntity manager = userRepo.findByLogin(managersLogin).get();

        mockMvc.perform(get("/users/{id}", manager.getId()).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.login", is(manager.getLogin())));
    }

    @Test
    @WithAnonymousUser
    void cannotCheckManagerProfile() throws Exception {
        UserEntity manager = userRepo.findByLogin(managersLogin).get();

        mockMvc.perform(get("/users/{id}", manager.getId()).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles="ADMIN")
    void AdminCreateInternSAcconunt() throws Exception {
        userDto = new UserDto();
        userDto.setLogin(internsLogin);
        userDto.setPassword("secret");

        mockMvc.perform(post("/users", userDto)
               .content(objectMapper.writeValueAsString(userDto)).contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.login", is(userDto.getLogin())));
    }

    @Test
    @WithMockUser(roles="MODER")
    void ModerCreateInternSAcconunt() throws Exception {
        mockMvc.perform(post("/users", userDto)
               .content(objectMapper.writeValueAsString(userDto)).contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles="ADMIN")
    void adminChangeUserRole() throws Exception {
        RoleEntity officialLetter = roleRepo.findByName("ROLE_MODER").get();
        UserEntity manager = userRepo.findByLogin(managersLogin).get();

        mockMvc.perform(patch("/roles/{role_id}/users/{id}", officialLetter.getId(), manager.getId())
            .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles="MODER")
    void moderChangeUserRole() throws Exception {
        RoleEntity officialLetter = roleRepo.findByName("ROLE_MODER").get();
        UserEntity manager = userRepo.findByLogin(managersLogin).get();

        mockMvc.perform(patch("/roles/{role_id}/users/{id}", officialLetter.getId(), manager.getId())
            .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles="ADMIN")
    void adminChangeUserPassword() throws Exception {
        UserEntity manager = userRepo.findByLogin(managersLogin).get();
        userDto = new UserDto();
        userDto.setPassword("new_password");

        mockMvc.perform(patch("/users/{id}", manager.getId(), userDto)
               .content(objectMapper.writeValueAsString(userDto)).contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles="MODER")
    void moderChangeUserPassword() throws Exception {
        UserEntity manager = userRepo.findByLogin(managersLogin).get();
        userDto = new UserDto();
        userDto.setPassword("new_password");

        mockMvc.perform(patch("/users/{id}", manager.getId(), userDto).contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles="ADMIN")
    void adminDeletesUser() throws Exception {
        userDto = new UserDto();
        userDto.setLogin(managersLogin);

        mockMvc.perform(delete("/users", userDto).content(objectMapper.writeValueAsString(userDto))
               .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles="MODER")
    void moderDeletesUser() throws Exception {
        userDto = new UserDto();
        userDto.setLogin(managersLogin);

        mockMvc.perform(delete("/users", userDto).content(objectMapper.writeValueAsString(userDto))
               .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
    }
}
