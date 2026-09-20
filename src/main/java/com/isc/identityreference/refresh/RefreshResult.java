package com.isc.identityreference.refresh;

import com.isc.identityreference.domain.identity.IdentityReference;

public record RefreshResult(Status status,IdentityReference reference,String message,String operationId){
    public enum Status { REFRESHED,NOT_FOUND,FAILED,SKIPPED,STALE_SERVED,LOCKED }
}