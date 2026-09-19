package com.isc.identityreference.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties({ApiSecurityProperties.class, ApiRateLimitProperties.class})
public class ApiSecurityConfiguration {

    @Bean
    SecurityFilterChain apiSecurityFilterChain(HttpSecurity http, ApiSecurityProperties properties) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        if (!properties.isEnabled()) {
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/identity/**").hasAnyRole("LOOKUP", "ADMIN")
                .anyRequest().permitAll())
                .httpBasic(basic -> {});

        return http.build();
    }

    @Bean
    UserDetailsService apiUsers(ApiSecurityProperties properties) {
        if (!properties.isEnabled()) {
            return new InMemoryUserDetailsManager(List.of());
        }

        List<org.springframework.security.core.userdetails.UserDetails> users = new ArrayList<>();
        addUser(users, properties.getLookupUsername(), properties.getLookupPassword(), "LOOKUP");
        addUser(users, properties.getAdminUsername(), properties.getAdminPassword(), "ADMIN");

        if (users.isEmpty()) {
            throw new IllegalStateException("API security is enabled but no API credentials are configured");
        }
        return new InMemoryUserDetailsManager(users);
    }

    private static void addUser(List<org.springframework.security.core.userdetails.UserDetails> users,
                                String username, String password, String role) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return;
        }
        users.add(User.withUsername(username)
                .password("{noop}" + password)
                .roles(role)
                .build());
    }
}
