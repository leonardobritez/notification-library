package com.example.notifications.model;

import java.util.Optional;

public record NotificationResult(String notificationId, NotificationStatus status, String message, Throwable error) {
    public boolean isSuccess() { return status == NotificationStatus.SENT; }
    public Optional<Throwable> errorOptional() { return Optional.ofNullable(error); }
}
