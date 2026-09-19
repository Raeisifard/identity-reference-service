package com.isc.identityreference.api;

public record AdminRefreshResponse(
        String status,
        String operationId,
        String message) {
}
