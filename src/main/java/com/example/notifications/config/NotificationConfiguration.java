package com.example.notifications.config;

import com.example.notifications.channel.*;
import com.example.notifications.event.NotificationPublisher;
import com.example.notifications.service.*;
import com.example.notifications.template.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/** Java-based configuration; gateways are adapters supplied by the consuming application. */
public final class NotificationConfiguration {
    private NotificationConfiguration() { }
    public static NotificationService create(EmailGateway email, SmsGateway sms, PushGateway push) {
        Map<String, MessageTemplate> templates = Map.of(
                "welcome", new WelcomeTemplate(),
                "security-alert", new SecurityAlertTemplate(),
                "custom-example", new SimpleTextTemplate("Hola {{name}}, tu código es {{code}}."));
        Executor executor = Executors.newFixedThreadPool(4);
        return new NotificationService(
                List.of(new EmailChannel(email), new SmsChannel(sms), new PushChannel(push)),
                templates, new NotificationPublisher(), RetryPolicy.noRetries(), executor);
    }
}
