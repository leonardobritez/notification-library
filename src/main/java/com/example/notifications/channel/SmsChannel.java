package com.example.notifications.channel;
import com.example.notifications.model.ChannelType;
import com.example.notifications.validation.RecipientValidator;
public class SmsChannel implements NotificationChannel {
    private final SmsGateway gateway;
    public SmsChannel(SmsGateway gateway) { this.gateway = java.util.Objects.requireNonNull(gateway); }
    @Override public ChannelType type() { return ChannelType.SMS; }
    @Override public void send(String recipient, String message) throws Exception {
        RecipientValidator.validatePhone(recipient); gateway.sendSms(recipient, message);
    }
}
