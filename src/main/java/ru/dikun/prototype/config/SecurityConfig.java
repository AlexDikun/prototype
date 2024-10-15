package ru.dikun.prototype.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.access.expression.SecurityExpressionHandler; 
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .securityContext((securityContext) -> securityContext.requireExplicitSave(true))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.GET, "/roleHierarchy")
                .hasRole("STAFF")
                .requestMatchers("/user")
                .hasAuthority("READ_PRIVELEGE")
                .requestMatchers("/admin", "/users/**", "/users", "/roles/{role_id}/users/{id}")
                .hasRole("ADMIN")
                .requestMatchers("/public")
                .anonymous());

        return http.build();
    }
    
    @Bean
    public SecurityExpressionHandler<FilterInvocation> customWebSecurityExpressionHandler() {
        DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy());
        return expressionHandler;
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        String hierarchy = "ROLE_ADMIN > ROLE_MODER \n ROLE_MODER > ROLE_STAFF";
        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }
}
