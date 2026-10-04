package com.isc.identityreference.security;

import com.isc.identityreference.admin.AdminConsoleProperties;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ApiSecurityConfigurationTest {

    @Test
    void devProfileNeverRequiresApiOrConsoleCredentials() {
        var environment = new MockEnvironment();
        environment.setActiveProfiles("dev");
        var api = new ApiSecurityProperties();
        api.setEnabled(true);
        var console = new AdminConsoleProperties();
        console.setEnabled(true);
        console.getAuthentication().setEnabled(true);

        var service = new ApiSecurityConfiguration().apiUsers(api, console, environment);

        assertThat(service).isNotNull();
    }

    @Test
    void defaultDevProfileNeverRequiresCredentialsWhenNoActiveProfileIsSelected() {
        var environment = new MockEnvironment();
        environment.setDefaultProfiles("dev");

        var api = new ApiSecurityProperties();
        api.setEnabled(true);
        var console = new AdminConsoleProperties();
        console.setEnabled(true);
        console.getAuthentication().setEnabled(true);

        var service = new ApiSecurityConfiguration().apiUsers(api, console, environment);

        assertThat(service).isNotNull();
    }

    @Test
    void protectedProfileRequiresCredentialsWhenSecurityIsEnabled() {
        var environment = new MockEnvironment();
        environment.setActiveProfiles("local");
        var api = new ApiSecurityProperties();
        api.setEnabled(true);
        var console = new AdminConsoleProperties();
        console.setEnabled(true);
        console.getAuthentication().setEnabled(true);

        assertThatThrownBy(() -> new ApiSecurityConfiguration().apiUsers(api, console, environment))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no API/admin credentials");
    }

    @Test
    void protectedProfileCanStartWithAdminCredentials() {
        var environment = new MockEnvironment();
        environment.setActiveProfiles("local");
        var api = new ApiSecurityProperties();
        api.setEnabled(true);
        api.setAdminUsername("admin");
        api.setAdminPassword("secret");
        var console = new AdminConsoleProperties();
        console.setEnabled(true);
        console.getAuthentication().setEnabled(true);

        var service = new ApiSecurityConfiguration().apiUsers(api, console, environment);

        assertThat(service).isNotNull();
    }
}
