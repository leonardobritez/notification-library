package com.example.notifications.channel;
public interface SmsGateway { void sendSms(String phoneNumber, String message) throws Exception; }
