package com.isc.identityreference.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "identity-reference.api.security")
public class ApiSecurityProperties {
    private boolean enabled;
    private String lookupUsername;
    private String lookupPassword;
    private String adminUsername;
    private String adminPassword;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getLookupUsername() { return lookupUsername; }
    public void setLookupUsername(String value) { this.lookupUsername = value; }
    public String getLookupPassword() { return lookupPassword; }
    public void setLookupPassword(String value) { this.lookupPassword = value; }
    public String getAdminUsername() { return adminUsername; }
    public void setAdminUsername(String value) { this.adminUsername = value; }
    public String getAdminPassword() { return adminPassword; }
    public void setAdminPassword(String value) { this.adminPassword = value; }
}
