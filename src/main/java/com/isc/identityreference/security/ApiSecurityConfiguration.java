
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

        boolean development = isDevelopmentProfile(environment);
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
                                "/admin-console/**",
                                "/api/v1/admin/test-data/**",
                                "/api/v1/admin/console/**"
                        ).hasRole("ADMIN");
                    } else {
                        auth.requestMatchers(
                                console.getPath(),
                                console.getPath() + "/**",
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
            http.formLogin(form -> form.successHandler((request, response, authentication) -> {
                var session = request.getSession(true);
                session.setMaxInactiveInterval((int) (console.getAuthentication().getSessionTimeoutMinutes() * 60));
                response.sendRedirect(console.getPath());
            }));
        }

        return http.build();
    }

    @Bean
    UserDetailsService apiUsers(
            ApiSecurityProperties properties,
            AdminConsoleProperties console,
            Environment environment
    ) {
        boolean development = isDevelopmentProfile(environment);
        boolean required =
                !development && (properties.isEnabled()
                        || (console.isEnabled()
                        && console.getAuthentication().isEnabled()));

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

    private static boolean isDevelopmentProfile(Environment environment) {
        // Explicitly active profiles always take precedence over the default
        // profile. This is important for tests and for protected local/oracle
        // deployments: @ActiveProfiles("test") must not inherit dev behavior.
        String[] activeProfiles = environment.getActiveProfiles();
        if (activeProfiles.length > 0) {
            return java.util.Arrays.stream(activeProfiles)
                    .anyMatch("dev"::equals);
        }

        // When nothing is explicitly active, this application deliberately
        // treats spring.profiles.default=dev as development mode.
        String defaultProfiles = environment.getProperty("spring.profiles.default", "");
        return java.util.Arrays.stream(defaultProfiles.split(","))
                .map(String::trim)
                .anyMatch("dev"::equals);
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