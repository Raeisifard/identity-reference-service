package com.isc.identityreference.admin;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class AdminConsolePropertiesTest {
    @Test
    void developmentAndIntegrationTestingAreDisabledByDefault() {
        var properties = new AdminConsoleProperties();
        assertThat(properties.isEnabled()).isFalse();
        assertThat(properties.isDevelopmentEnabled()).isFalse();
        assertThat(properties.isIntegrationTestingEnabled()).isFalse();
    }
}
