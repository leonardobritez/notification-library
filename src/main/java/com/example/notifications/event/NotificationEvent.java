package com.example.notifications.event;

import com.example.notifications.model.ChannelType;
import com.example.notifications.model.NotificationStatus;
import java.time.Instant;

public record NotificationEvent(String notificationId, ChannelType channel, NotificationStatus status,
                                String message, Instant timestamp, String errorType) { }
