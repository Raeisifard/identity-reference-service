package com.isc.identityreference.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record AdminRetireRequest(
        @NotBlank String nationalId,
        @NotNull LocalDate birthDate,
        @NotBlank String reasonCode) {
}
