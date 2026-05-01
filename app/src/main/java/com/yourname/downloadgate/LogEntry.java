package com.yourname.downloadgate;

/**
 * Model untuk satu entri intercept log.
 */
public class LogEntry {
    public final String callerPackage;
    public final String uri;
    public final long timestamp;
    public final boolean success;

    public LogEntry(String callerPackage, String uri, long timestamp, boolean success) {
        this.callerPackage = callerPackage;
        this.uri           = uri;
        this.timestamp     = timestamp;
        this.success       = success;
    }
}
