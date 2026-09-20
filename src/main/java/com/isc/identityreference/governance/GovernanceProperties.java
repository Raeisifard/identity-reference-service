package com.isc.identityreference.governance;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.time.Duration;
@ConfigurationProperties(prefix="identity-reference.governance")
public class GovernanceProperties {
 private boolean enabled=true; private Duration identityRetention=Duration.ofDays(365),retiredRetention=Duration.ofDays(30),auditRetention=Duration.ofDays(730),purgeDelay=Duration.ofHours(1); private int purgeBatchSize=1000;
 public boolean isEnabled(){return enabled;} public void setEnabled(boolean v){enabled=v;}
 public Duration getIdentityRetention(){return identityRetention;} public void setIdentityRetention(Duration v){identityRetention=requirePositive(v,"identity-retention");}
 public Duration getRetiredRetention(){return retiredRetention;} public void setRetiredRetention(Duration v){retiredRetention=requirePositive(v,"retired-retention");}
 public Duration getAuditRetention(){return auditRetention;} public void setAuditRetention(Duration v){auditRetention=requirePositive(v,"audit-retention");}
 public Duration getPurgeDelay(){return purgeDelay;} public void setPurgeDelay(Duration v){purgeDelay=requirePositive(v,"purge-delay");}
 public int getPurgeBatchSize(){return purgeBatchSize;} public void setPurgeBatchSize(int v){if(v<=0)throw new IllegalArgumentException("purge-batch-size must be positive");purgeBatchSize=v;}
 private static Duration requirePositive(Duration v,String n){if(v==null||v.isZero()||v.isNegative())throw new IllegalArgumentException(n+" must be positive");return v;}
}
