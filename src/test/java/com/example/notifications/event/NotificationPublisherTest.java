package com.example.notifications.event;
import com.example.notifications.model.*;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;
class NotificationPublisherTest {
 @Test void notifiesSubscribers() {
   NotificationPublisher publisher = new NotificationPublisher(); AtomicInteger calls = new AtomicInteger();
   publisher.subscribe(event -> calls.incrementAndGet());
   publisher.publish(new NotificationEvent("id", ChannelType.EMAIL, NotificationStatus.SENT, "ok", Instant.now(), null));
   assertEquals(1, calls.get());
 }
 @Test void listenerFailureDoesNotStopOtherListeners() {
   AtomicInteger errors = new AtomicInteger(); AtomicInteger called = new AtomicInteger();
   NotificationPublisher publisher = new NotificationPublisher(e -> errors.incrementAndGet());
   publisher.subscribe(event -> { throw new IllegalStateException("broken listener"); });
   publisher.subscribe(event -> called.incrementAndGet());
   publisher.publish(new NotificationEvent("id", ChannelType.EMAIL, NotificationStatus.SENT, "ok", Instant.now(), null));
   assertEquals(1, errors.get()); assertEquals(1, called.get());
 }
}
