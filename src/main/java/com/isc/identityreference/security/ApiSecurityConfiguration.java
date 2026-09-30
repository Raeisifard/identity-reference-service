package com.isc.identityreference.security;

import com.isc.identityreference.admin.AdminConsoleProperties;
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
@EnableConfigurationProperties({ApiSecurityProperties.class, ApiRateLimitProperties.class,
        com.isc.identityreference.observability.ObservabilityProperties.class})
public class ApiSecurityConfiguration {
    @Bean ApiRateLimitGuard apiRateLimitGuard(ApiRateLimitProperties properties){return new ApiRateLimitGuard(properties);}
    @Bean SecurityFilterChain apiSecurityFilterChain(HttpSecurity http, ApiSecurityProperties properties,
                                                     AdminConsoleProperties console) throws Exception {
        boolean apiProtected=properties.isEnabled();
        boolean consoleProtected=console.isEnabled() && console.getAuthentication().isEnabled();
        http.csrf(csrf->csrf.disable())
            .sessionManagement(s->s.sessionCreationPolicy(consoleProtected?SessionCreationPolicy.IF_REQUIRED:SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a->{
                a.requestMatchers("/actuator/health","/actuator/health/**").permitAll();
                if(consoleProtected) a.requestMatchers("/admin-console/**","/api/v1/admin/test-data/**","/api/v1/admin/console/**").hasRole("ADMIN");
                else a.requestMatchers("/admin-console/**","/api/v1/admin/test-data/**","/api/v1/admin/console/**").permitAll();
                if(apiProtected){
                    a.requestMatchers("/api/v1/admin/**").hasRole("ADMIN");
                    a.requestMatchers("/api/v1/identity/**").hasAnyRole("LOOKUP","ADMIN");
                }
                a.anyRequest().permitAll();
            });
        if(apiProtected||consoleProtected) http.httpBasic(b->{});
        return http.build();
    }
    @Bean UserDetailsService apiUsers(ApiSecurityProperties properties,AdminConsoleProperties console){
        boolean required=properties.isEnabled()||(console.isEnabled()&&console.getAuthentication().isEnabled());
        if(!required)return new InMemoryUserDetailsManager(List.of());
        List<org.springframework.security.core.userdetails.UserDetails> users=new ArrayList<>();
        addUser(users,properties.getLookupUsername(),properties.getLookupPassword(),"LOOKUP");
        addUser(users,properties.getAdminUsername(),properties.getAdminPassword(),"ADMIN");
        if(users.isEmpty())throw new IllegalStateException("Security is enabled but no API/admin credentials are configured");
        return new InMemoryUserDetailsManager(users);
    }
    private static void addUser(List<org.springframework.security.core.userdetails.UserDetails> users,String username,String password,String role){
        if(username==null||username.isBlank()||password==null||password.isBlank())return;
        users.add(User.withUsername(username).password("{noop}"+password).roles(role).build());
    }
}
