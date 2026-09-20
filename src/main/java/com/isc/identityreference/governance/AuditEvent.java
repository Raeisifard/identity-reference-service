package com.isc.identityreference.governance;
import java.time.Instant; import java.util.UUID;
public record AuditEvent(UUID eventId,String lookupKeyHash,String eventType,String actorType,String reasonCode,String providerId,Long policyVersion,String fromState,String toState,UUID operationId,Instant occurredAt){}
