package com.example.notifications.channel;
public interface PushGateway { void sendPush(String deviceToken, String message) throws Exception; }
