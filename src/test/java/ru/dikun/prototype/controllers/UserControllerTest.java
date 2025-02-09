package ru.dikun.prototype.controllers;

import java.util.Collections;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;


import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.BDDMockito.given;

import ru.dikun.prototype.PrototypeApplication;
import ru.dikun.prototype.domain.UserEntity;
import ru.dikun.prototype.repos.RoleRepo;
import ru.dikun.prototype.repos.UserRepo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = PrototypeApplication.class)
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude=SecurityAutoConfiguration.class)
@TestPropertySource(locations = "classpath:application.properties")
public class UserControllerTest {

    @Autowired
    UserRepo userRepo;

    @Autowired
    RoleRepo roleRepo;

    @Autowired
    PasswordEncoder passwordEncoder; 

    @Autowired
    MockMvc mockMvc;

    private String login = "manager@company.ru";

    @BeforeEach
    private void setup() {

        UserEntity manager = new UserEntity();
        manager.setLogin("manager@company.ru");
        manager.setPassword(passwordEncoder.encode("secret"));
        manager.setRoles(Collections.singletonList(roleRepo.findByName("ROLE_STAFF").get()));
        userRepo.save(manager);

    }
    @AfterEach
    public void resetDb() {
        UserEntity manager = userRepo.findByLogin(login).get();
        userRepo.delete(manager);
    }

    @Test
    @WithMockUser(roles="ADMIN")
    void checkManagerProfile() throws Exception {
        UserEntity manager = userRepo.findByLogin(login).get();

        mockMvc.perform(get("/users/{id}", manager.getId()).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.login", is(manager.getLogin())));
    }

    @Test
    @WithAnonymousUser
    void cannotCheckManagerProfile() throws Exception {
        UserEntity manager = userRepo.findByLogin(login).get();

        mockMvc.perform(get("/users/{id}", manager.getId()).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());
    }
    
}
