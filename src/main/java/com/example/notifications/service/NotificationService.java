package com.example.notifications.service;

import com.example.notifications.channel.NotificationChannel;
import com.example.notifications.event.NotificationEvent;
import com.example.notifications.event.NotificationPublisher;
import com.example.notifications.exception.NotificationException;
import com.example.notifications.model.*;
import com.example.notifications.template.MessageTemplate;
import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class NotificationService {
    private final Map<ChannelType, NotificationChannel> channels = new EnumMap<>(ChannelType.class);
    private final Map<String, MessageTemplate> templates;
    private final NotificationPublisher publisher;
    private final RetryPolicy retryPolicy;
    private final Executor executor;

    public NotificationService(Iterable<NotificationChannel> channels, Map<String, MessageTemplate> templates,
                               NotificationPublisher publisher, RetryPolicy retryPolicy, Executor executor) {
        for (NotificationChannel channel : channels) {
            if (this.channels.put(channel.type(), channel) != null) throw new IllegalArgumentException("Duplicate channel: " + channel.type());
        }
        this.templates = Map.copyOf(templates);
        this.publisher = java.util.Objects.requireNonNull(publisher);
        this.retryPolicy = java.util.Objects.requireNonNull(retryPolicy);
        this.executor = java.util.Objects.requireNonNull(executor);
        for (ChannelType type : ChannelType.values()) if (!this.channels.containsKey(type))
            throw new IllegalArgumentException("Missing required channel: " + type);
    }

    public NotificationResult send(NotificationRequest request) {
        String id = UUID.randomUUID().toString();
        publish(id, request.channel(), NotificationStatus.PENDING, "Notification processing started", null);
        try {
            MessageTemplate template = templates.get(request.templateName());
            if (template == null) throw new IllegalArgumentException("Unknown template: " + request.templateName());
            String message = template.render(request.variables());
            NotificationChannel channel = channels.get(request.channel());
            Exception lastError = null;
            for (int attempt = 1; attempt <= retryPolicy.maxAttempts(); attempt++) {
                try {
                    channel.send(request.recipient(), message);
                    publish(id, request.channel(), NotificationStatus.SENT, "Provider accepted the notification", null);
                    return new NotificationResult(id, NotificationStatus.SENT, "Provider accepted the notification", null);
                } catch (Exception error) {
                    lastError = error;
                }
            }
            publish(id, request.channel(), NotificationStatus.FAILED, "Notification send failed", lastError);
            return new NotificationResult(id, NotificationStatus.FAILED, "Notification send failed", lastError);
        } catch (RuntimeException error) {
            publish(id, request.channel(), NotificationStatus.FAILED, "Notification processing failed", error);
            return new NotificationResult(id, NotificationStatus.FAILED, "Notification processing failed", error);
        }
    }

    public CompletableFuture<NotificationResult> sendAsync(NotificationRequest request) {
        return CompletableFuture.supplyAsync(() -> send(request), executor);
    }

    private void publish(String id, ChannelType channel, NotificationStatus status, String message, Throwable error) {
        publisher.publish(new NotificationEvent(id, channel, status, message, Instant.now(),
                error == null ? null : error.getClass().getSimpleName()));
    }
}
