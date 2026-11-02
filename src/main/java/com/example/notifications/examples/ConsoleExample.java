package com.example.notifications.examples;

import com.example.notifications.config.NotificationConfiguration;
import com.example.notifications.channel.*;
import com.example.notifications.event.NotificationListener;
import com.example.notifications.model.*;
import com.example.notifications.service.NotificationService;
import java.util.Map;

public class ConsoleExample {
    public static void main(String[] args) {
        EmailGateway email = (recipient, message) -> System.out.println("[EMAIL to " + recipient + "] " + message);
        SmsGateway sms = (recipient, message) -> System.out.println("[SMS to " + recipient + "] " + message);
        PushGateway push = (recipient, message) -> System.out.println("[PUSH to " + recipient + "] " + message);
        NotificationService service = NotificationConfiguration.create(email, sms, push);
        // In a real application, expose the publisher from your DI configuration and register listeners there.
        NotificationResult result = service.send(new NotificationRequest(ChannelType.EMAIL,
                "demo@example.com", "welcome", Map.of("name", "Leonardo")));
        System.out.println("Result: " + result.status());
    }
}
