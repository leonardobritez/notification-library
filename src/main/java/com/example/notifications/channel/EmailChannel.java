package com.example.notifications.channel;
import com.example.notifications.model.ChannelType;
import com.example.notifications.validation.RecipientValidator;
public class EmailChannel implements NotificationChannel {
    private final EmailGateway gateway;
    public EmailChannel(EmailGateway gateway) { this.gateway = java.util.Objects.requireNonNull(gateway); }
    @Override public ChannelType type() { return ChannelType.EMAIL; }
    @Override public void send(String recipient, String message) throws Exception {
        RecipientValidator.validateEmail(recipient); gateway.sendEmail(recipient, message);
    }
}
