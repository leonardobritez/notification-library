package com.example.notifications.model;

import java.util.Map;
import java.util.Objects;

/** A channel-specific destination plus a template name and its variables. */
public record NotificationRequest(ChannelType channel, String recipient, String templateName,
                                  Map<String, Object> variables) {
    public NotificationRequest {
        Objects.requireNonNull(channel, "channel is required");
        if (recipient == null || recipient.isBlank()) throw new IllegalArgumentException("recipient is required");
        if (templateName == null || templateName.isBlank()) throw new IllegalArgumentException("templateName is required");
        variables = variables == null ? Map.of() : Map.copyOf(variables);
    }
}
