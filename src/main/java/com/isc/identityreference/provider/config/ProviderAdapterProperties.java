package com.isc.identityreference.provider.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ConfigurationProperties(prefix = "identity-reference.provider")
public class ProviderAdapterProperties {
    private boolean realIntegrationsEnabled;
    private List<Adapter> adapters = new ArrayList<>();

    public boolean isRealIntegrationsEnabled() { return realIntegrationsEnabled; }
    public void setRealIntegrationsEnabled(boolean value) { this.realIntegrationsEnabled = value; }

    public List<Adapter> getAdapters() { return adapters; }
    public void setAdapters(List<Adapter> value) {
        this.adapters = new ArrayList<>(Objects.requireNonNull(value, "adapters"));
    }

    public static class Adapter {
        private String providerId;
        private String displayName;
        private String type;
        private boolean enabled;

        public String getProviderId() { return providerId; }
        public void setProviderId(String value) { providerId = value; }
        public String getDisplayName() { return displayName; }
        public void setDisplayName(String value) { displayName = value; }
        public String getType() { return type; }
        public void setType(String value) { type = value; }
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean value) { enabled = value; }
    }
}
