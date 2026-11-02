package com.example.notifications.service;

import com.example.notifications.channel.*;
import com.example.notifications.event.*;
import com.example.notifications.model.*;
import com.example.notifications.template.MessageTemplate;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.concurrent.Executor;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {
 private final EmailGateway email = mock(EmailGateway.class);
 private final SmsGateway sms = mock(SmsGateway.class);
 private final PushGateway push = mock(PushGateway.class);
 private final NotificationPublisher publisher = new NotificationPublisher(error -> { });
 private NotificationService service(RetryPolicy retries) {
   return new NotificationService(List.of(new EmailChannel(email), new SmsChannel(sms), new PushChannel(push)),
      Map.of("welcome", (MessageTemplate) vars -> "Hello " + vars.get("name")), publisher, retries, Runnable::run);
 }
 private NotificationRequest request() { return new NotificationRequest(ChannelType.EMAIL, "a@b.com", "welcome", Map.of("name", "Leo")); }
 @Test void publishesPendingThenSentOnSuccess() throws Exception {
   List<NotificationEvent> events = new ArrayList<>(); publisher.subscribe(events::add);
   doNothing().when(email).sendEmail("a@b.com", "Hello Leo");
   NotificationResult result = service(RetryPolicy.noRetries()).send(request());
   assertEquals(NotificationStatus.SENT, result.status());
   assertEquals(List.of(NotificationStatus.PENDING, NotificationStatus.SENT), events.stream().map(NotificationEvent::status).toList());
   assertEquals(result.notificationId(), events.get(1).notificationId());
 }
 @Test void publishesFailedOnProviderError() throws Exception {
   List<NotificationEvent> events = new ArrayList<>(); publisher.subscribe(events::add);
   doThrow(new Exception("provider down")).when(email).sendEmail("a@b.com", "Hello Leo");
   NotificationResult result = service(RetryPolicy.noRetries()).send(request());
   assertEquals(NotificationStatus.FAILED, result.status());
   assertEquals(NotificationStatus.FAILED, events.get(events.size()-1).status());
   assertNotNull(result.error());
 }
 @Test void retriesConfiguredNumberOfTimes() throws Exception {
   doThrow(new Exception("temporary")).when(email).sendEmail("a@b.com", "Hello Leo");
   NotificationResult result = service(new RetryPolicy(3)).send(request());
   assertEquals(NotificationStatus.FAILED, result.status()); verify(email, times(3)).sendEmail("a@b.com", "Hello Leo");
 }
 @Test void unknownTemplateFailsAndPublishesFailed() {
   List<NotificationEvent> events = new ArrayList<>(); publisher.subscribe(events::add);
   NotificationResult result = service(RetryPolicy.noRetries()).send(new NotificationRequest(ChannelType.EMAIL, "a@b.com", "missing", Map.of()));
   assertEquals(NotificationStatus.FAILED, result.status()); assertEquals(NotificationStatus.FAILED, events.get(events.size()-1).status());
 }
 @Test void asyncSendReturnsResult() {
   NotificationResult result = service(RetryPolicy.noRetries()).sendAsync(request()).join();
   assertEquals(NotificationStatus.SENT, result.status());
 }
}
