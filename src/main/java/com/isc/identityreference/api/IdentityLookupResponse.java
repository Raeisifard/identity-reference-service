package com.isc.identityreference.api;

public record IdentityLookupResponse(
        String status,
        String providerId,
        String message) {
}
