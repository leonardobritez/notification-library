package com.example.notifications.service;
/** maxAttempts includes the first attempt; use 1 to disable retries. */
public record RetryPolicy(int maxAttempts) {
    public RetryPolicy { if (maxAttempts < 1) throw new IllegalArgumentException("maxAttempts must be >= 1"); }
    public static RetryPolicy noRetries() { return new RetryPolicy(1); }
}
