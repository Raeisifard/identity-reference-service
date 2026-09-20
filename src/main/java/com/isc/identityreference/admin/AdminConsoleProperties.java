package com.isc.identityreference.admin;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "identity-reference.admin-console")
public class AdminConsoleProperties {
    private boolean enabled;
    private boolean developmentEnabled;
    private boolean integrationTestingEnabled;
    private String title = "Identity Reference Console";

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public boolean isDevelopmentEnabled() { return developmentEnabled; }
    public void setDevelopmentEnabled(boolean developmentEnabled) { this.developmentEnabled = developmentEnabled; }
    public boolean isIntegrationTestingEnabled() { return integrationTestingEnabled; }
    public void setIntegrationTestingEnabled(boolean integrationTestingEnabled) { this.integrationTestingEnabled = integrationTestingEnabled; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}
