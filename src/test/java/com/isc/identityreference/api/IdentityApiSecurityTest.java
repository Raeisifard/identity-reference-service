package com.isc.identityreference.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "identity-reference.api.security.enabled=true",
        "identity-reference.api.security.lookup-username=lookup",
        "identity-reference.api.security.lookup-password=lookup-secret",
        "identity-reference.api.security.admin-username=admin",
        "identity-reference.api.security.admin-password=admin-secret",
        "identity-reference.api.rate-limit.enabled=false"
})
class IdentityApiSecurityTest {
    @Autowired MockMvc mockMvc;

    @Test
    void lookupRequiresAuthentication() throws Exception {
        mockMvc.perform(post("/api/v1/identity/lookup")
                        .contentType("application/json")
                        .content("{\"nationalId\":\"FIXTURE-FOUND-001\",\"birthDate\":\"1990-01-01\",\"providerId\":\"mock-national-agency\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void lookupReturnsGenericFoundResult() throws Exception {
        mockMvc.perform(post("/api/v1/identity/lookup")
                        .with(httpBasic("lookup", "lookup-secret"))
                        .contentType("application/json")
                        .content("{\"nationalId\":\"FIXTURE-FOUND-001\",\"birthDate\":\"1990-01-01\",\"providerId\":\"mock-national-agency\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void adminEndpointRequiresAdminRole() throws Exception {
        mockMvc.perform(get("/api/v1/admin/status").with(httpBasic("lookup", "lookup-secret")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminEndpointAcceptsAdminRole() throws Exception {
        mockMvc.perform(get("/api/v1/admin/status").with(httpBasic("admin", "admin-secret")))
                .andExpect(status().isOk());
    }
}
