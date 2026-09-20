package com.isc.identityreference.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component public class IdentityReferenceMetrics {
 private final MeterRegistry registry; public IdentityReferenceMetrics(MeterRegistry registry){this.registry=registry;}
 public Timer lookupTimer(String providerId){return Timer.builder("identity_reference_lookup_duration").description("Identity lookup duration").tag("provider",safe(providerId)).register(registry);}
 public void lookup(String providerId,String outcome){Counter.builder("identity_reference_lookup_total").description("Identity lookup requests").tag("provider",safe(providerId)).tag("outcome",safe(outcome)).register(registry).increment();}
 public void rateLimited(){Counter.builder("identity_reference_api_rate_limited_total").description("API requests rejected by the rate limiter").register(registry).increment();}
 public void refreshAccepted(String providerId){refreshOutcome(providerId,"admin","accepted");}
 public void refreshOutcome(String providerId,String trigger,String outcome){Counter.builder("identity_reference_refresh_total").description("Identity reference refresh operations").tag("provider",safe(providerId)).tag("trigger",safe(trigger)).tag("outcome",safe(outcome)).register(registry).increment();}
 public void refreshSkipped(String providerId,String reason){refreshOutcome(providerId,"automatic",reason);}
 public void refreshDuration(String providerId,String trigger,long nanos){Timer.builder("identity_reference_refresh_duration").description("Identity reference refresh duration").tag("provider",safe(providerId)).tag("trigger",safe(trigger)).register(registry).record(nanos,java.util.concurrent.TimeUnit.NANOSECONDS);}
 private static String safe(String v){return v==null||v.isBlank()?"unknown":v;}
}