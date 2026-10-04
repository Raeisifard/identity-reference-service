
package com.isc.identityreference.security;

import com.isc.identityreference.admin.AdminConsoleProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties({
        ApiSecurityProperties.class,
        ApiRateLimitProperties.class,
        com.isc.identityreference.observability.ObservabilityProperties.class
})
public class ApiSecurityConfiguration {

    @Bean
    ApiRateLimitGuard apiRateLimitGuard(ApiRateLimitProperties properties) {
        return new ApiRateLimitGuard(properties);
    }

    @Bean
    SecurityFilterChain apiSecurityFilterChain(
            HttpSecurity http,
            ApiSecurityProperties properties,
            AdminConsoleProperties console,
            Environment environment
    ) throws Exception {

        boolean development = environment.matchesProfiles("dev");
        boolean apiProtected = !development && properties.isEnabled();
        boolean consoleProtected =
                !development
                        && console.isEnabled()
                        && console.getAuthentication().isEnabled();

        http
                .headers(headers -> {
                    if (development) {
                        headers.frameOptions(frame -> frame.sameOrigin());
                    }
                })
                .csrf(csrf -> {
                    if (development) {
                        csrf.ignoringRequestMatchers("/h2-console/**");
                    }
                    csrf.disable();
                })
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                consoleProtected
                                        ? SessionCreationPolicy.IF_REQUIRED
                                        : SessionCreationPolicy.STATELESS
                        )
                )
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(
                            "/actuator/health",
                            "/actuator/health/**"
                    ).permitAll();

                    if (development) {
                        auth.requestMatchers("/h2-console/**").permitAll();
                    }

                    if (consoleProtected) {
                        auth.requestMatchers(
                                console.getPath(),
                                console.getPath() + "/**",
                                console.getPath(),
                                console.getPath() + "/**",
                                "/admin-console/**",
                                "/api/v1/admin/test-data/**",
                                "/api/v1/admin/console/**"
                        ).hasRole("ADMIN");
                    } else {
                        auth.requestMatchers(
                                "/admin-console/**",
                                "/api/v1/admin/test-data/**",
                                "/api/v1/admin/console/**"
                        ).permitAll();
                    }

                    if (apiProtected) {
                        auth.requestMatchers("/api/v1/admin/**")
                                .hasRole("ADMIN");

                        auth.requestMatchers("/api/v1/identity/**")
                                .hasAnyRole("LOOKUP", "ADMIN");
                    }

                    auth.anyRequest().permitAll();
                });

        if (apiProtected || consoleProtected) {
            http.httpBasic(basic -> {});
        }
        if (consoleProtected && console.getAuthentication().isLoginPageEnabled()) {
            http.formLogin(form -> form.defaultSuccessUrl(console.getPath(), true));
        }

        return http.build();
    }

    @Bean
    UserDetailsService apiUsers(
            ApiSecurityProperties properties,
            AdminConsoleProperties console,
            Environment environment
    ) {
        boolean development = environment.matchesProfiles("dev");
        boolean required =
                !development && (properties.isEnabled()
                        || (console.isEnabled()
                        && console.getAuthentication().isEnabled());

        if (!required) {
            return new InMemoryUserDetailsManager(List.of());
        }

        List<org.springframework.security.core.userdetails.UserDetails> users =
                new ArrayList<>();

        addUser(
                users,
                properties.getLookupUsername(),
                properties.getLookupPassword(),
                "LOOKUP"
        );

        addUser(
                users,
                properties.getAdminUsername(),
                properties.getAdminPassword(),
                "ADMIN"
        );

        if (users.isEmpty()) {
            throw new IllegalStateException(
                    "Security is enabled but no API/admin credentials are configured"
            );
        }

        return new InMemoryUserDetailsManager(users);
    }

    private static void addUser(
            List<org.springframework.security.core.userdetails.UserDetails> users,
            String username,
            String password,
            String role
    ) {
        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {
            return;
        }

        users.add(
                User.withUsername(username)
                        .password("{noop}" + password)
                        .roles(role)
                        .build()
        );
    }
}