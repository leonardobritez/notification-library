package com.example.notifications.channel;
import com.example.notifications.model.ChannelType;
public class PushChannel implements NotificationChannel {
    private final PushGateway gateway;
    public PushChannel(PushGateway gateway) { this.gateway = java.util.Objects.requireNonNull(gateway); }
    @Override public ChannelType type() { return ChannelType.PUSH; }
    @Override public void send(String recipient, String message) throws Exception {
        if (recipient == null || recipient.isBlank()) throw new IllegalArgumentException("device token is required");
        gateway.sendPush(recipient, message);
    }
}
