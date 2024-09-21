package ru.dikun.prototype.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ru.dikun.prototype.domain.PrivilegeEntity;
import ru.dikun.prototype.domain.RoleEntity;
import ru.dikun.prototype.domain.UserEntity;
import ru.dikun.prototype.repos.PrivilegeRepo;
import ru.dikun.prototype.repos.RoleRepo;
import ru.dikun.prototype.repos.UserRepo;

import java.util.List;
import java.util.Optional;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;

    @Autowired
    UserRepo userRepo;

    @Autowired
    RoleRepo roleRepo;

    @Autowired
    PrivilegeRepo privilegeRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Transactional
    public PrivilegeEntity createPrivilege(final String name) {
        Optional<PrivilegeEntity> optPrivilege = privilegeRepo.findByName(name);
        PrivilegeEntity privilege;

        if (optPrivilege.isPresent()) 
            privilege = optPrivilege.get();
        else
            privilege = new PrivilegeEntity(name);

        return privilege;
    }

    @Transactional
    public RoleEntity createRole(final String name, final Collection<PrivilegeEntity> privileges) {
        Optional<RoleEntity> optRole = roleRepo.findByName(name);
        RoleEntity role;

        if (optRole.isPresent())
            role = optRole.get();
        else
            role = new RoleEntity(name);

        return role;
    }

    @Transactional
    public UserEntity createUser(final String login, final String password, final Collection<RoleEntity> roles) {
        Optional<UserEntity> optUser = userRepo.findByLogin(login);
        UserEntity user;

        if (optUser.isPresent())
            user = optUser.get();
        else
            user = new UserEntity();
            user.setLogin(login);
            user.setPassword(passwordEncoder.encode(password));

        user.setRoles(roles);
        user = userRepo.save(user);
        return user;
    }

    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }

        // == create init pirivileges
        final PrivilegeEntity readPrivilege = createPrivilege("READ_PRIVILEGE");
        final PrivilegeEntity writePrivilege = createPrivilege("WRITE_PRIVILEGE");
        final PrivilegeEntity passwordPrivilege = createPrivilege("CHANGE_PASSWORD_PRIVILEGE");

        // == create init roles
        final List<PrivilegeEntity> admPrivileges = new ArrayList<>(Arrays.asList(readPrivilege, writePrivilege, passwordPrivilege));
        final List<PrivilegeEntity> stuffPrivileges = new ArrayList<>(Arrays.asList(readPrivilege, passwordPrivilege));

        final RoleEntity adminRole = createRole("ROLE_ADMIN", admPrivileges);
        createRole("ROLE_STUFF", stuffPrivileges);

        // == create init users FIRST ADMIN in db
        createUser("ADMIN", "ADMIN", new ArrayList<>(Arrays.asList(adminRole)));

        alreadySetup = true;
    }
}
    
    