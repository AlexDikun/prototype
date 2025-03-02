package ru.dikun.prototype.sevrice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.dikun.prototype.domain.PrivilegeEntity;
import ru.dikun.prototype.domain.RoleEntity;
import ru.dikun.prototype.domain.UserEntity;
import ru.dikun.prototype.repos.RoleRepo;
import ru.dikun.prototype.repos.UserRepo;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

@Service
@Transactional
public class UserService implements UserDetailsService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    RoleRepo roleRepo;

    @Override
    public UserDetails loadUserByUsername(final String login) {
        final Optional<UserEntity> optUser = userRepo.findByLogin(login);
        if (optUser.isEmpty()) {
            throw new UsernameNotFoundException("No user found with username: " + login);
        }
        UserEntity user = optUser.get();
        return new org.springframework.security.core.userdetails.User(
            user.getLogin(), user.getPassword(), true, true, true, true, getAuthorities(user.getRoles()));
    }

    private List<GrantedAuthority> getGrantedAuthorities(final List<String> privileges) {
        final List<GrantedAuthority> authorities = new ArrayList<>();

        for (final String privelege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privelege));
        }

        return authorities;
    }

    private List<String> getPrivileges(final Collection<RoleEntity> roles) {
        final List<String> privileges = new ArrayList<>();
        final List<PrivilegeEntity> collection = new ArrayList<>();

        if (roles != null) {
            for (final RoleEntity role : roles) {
                if (role != null && role.getName() != null) {
                    privileges.add(role.getName());
                }
                if (role.getPrivileges() != null) {
                    collection.addAll(role.getPrivileges());
                }
            }
        }
    
        for (final PrivilegeEntity item : collection) {
            if (item != null && item.getName() != null) {
                privileges.add(item.getName());
            }
        }

        return privileges;
    }

    private Collection<? extends GrantedAuthority> getAuthorities(final Collection<RoleEntity> roles) {
        return getGrantedAuthorities(getPrivileges(roles));
    }

    // Геттеры для приватных методов
    public List<GrantedAuthority> getGrantedAuthoritiesPublic(final List<String> privileges) {
        return getGrantedAuthorities(privileges);
    }

    public List<String> getPrivilegesPublic(final Collection<RoleEntity> roles) {
        return getPrivileges(roles);
    }

    public Collection<? extends GrantedAuthority> getAuthoritiesPublic(final Collection<RoleEntity> roles) {
        return getAuthorities(roles);
    }
}
