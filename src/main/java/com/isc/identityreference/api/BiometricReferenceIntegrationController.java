package com.isc.identityreference.api;

import com.isc.identityreference.domain.biometric.EmbeddingMetric;
import com.isc.identityreference.integration.BiometricReferenceIntegrationService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/biometric/references")
public class BiometricReferenceIntegrationController {
    private final BiometricReferenceIntegrationService service;

    public BiometricReferenceIntegrationController(BiometricReferenceIntegrationService service) {
        this.service = service;
    }

    @GetMapping("/{identityReferenceId}")
    public ResponseEntity<BiometricReferenceIntegrationResponse> getReference(
            @PathVariable UUID identityReferenceId,
            @RequestParam @NotBlank String modelId,
            @RequestParam @NotBlank String modelVersion,
            @RequestParam @Min(1) int dimension,
            @RequestParam EmbeddingMetric metric,
            @RequestParam boolean normalized) {

        return service.find(identityReferenceId, modelId, modelVersion, dimension, metric, normalized)
                .map(reference -> ResponseEntity.ok(BiometricReferenceIntegrationResponse.from(identityReferenceId, reference)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
