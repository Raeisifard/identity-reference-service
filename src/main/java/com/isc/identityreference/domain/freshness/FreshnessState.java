package com.isc.identityreference.domain.freshness;

/** Freshness classification for acquired reference data. */
public enum FreshnessState {
    UNKNOWN,
    FRESH,
    STALE,
    EXPIRED
}
