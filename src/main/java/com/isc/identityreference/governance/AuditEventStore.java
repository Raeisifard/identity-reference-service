package com.isc.identityreference.governance;
import java.time.Instant; import java.util.List;
public interface AuditEventStore { void append(AuditEvent event); default int purgeBefore(Instant cutoff,int limit){return 0;} default List<AuditEvent> recent(int limit){return List.of();} }
