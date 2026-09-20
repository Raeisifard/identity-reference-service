package com.isc.identityreference.api;

import jakarta.validation.constraints.NotBlank;

public record AdminRefreshRequest(
        @NotBlank String providerId) {
}
