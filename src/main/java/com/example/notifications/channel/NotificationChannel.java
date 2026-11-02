package com.example.notifications.channel;

import com.example.notifications.model.ChannelType;
public interface NotificationChannel {
    ChannelType type();
    /** Return only when the provider accepts the send; throw an exception on failure. */
    void send(String recipient, String message) throws Exception;
}
