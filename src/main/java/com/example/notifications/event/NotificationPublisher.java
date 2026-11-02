package com.example.notifications.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/** In-process Observer publisher. Listener exceptions are isolated from other listeners. */
public class NotificationPublisher {
    private final CopyOnWriteArrayList<NotificationListener> listeners = new CopyOnWriteArrayList<>();
    private final Consumer<Throwable> listenerErrorHandler;

    public NotificationPublisher() { this(error -> System.err.println("Notification listener failed: " + error)); }
    public NotificationPublisher(Consumer<Throwable> listenerErrorHandler) {
        this.listenerErrorHandler = listenerErrorHandler == null ? error -> { } : listenerErrorHandler;
    }
    public void subscribe(NotificationListener listener) {
        if (listener == null) throw new IllegalArgumentException("listener is required");
        listeners.addIfAbsent(listener);
    }
    public void unsubscribe(NotificationListener listener) { listeners.remove(listener); }
    public void publish(NotificationEvent event) {
        for (NotificationListener listener : listeners) {
            try { listener.onNotificationEvent(event); }
            catch (RuntimeException exception) {
                try { listenerErrorHandler.accept(exception); } catch (RuntimeException ignored) { /* isolate handler too */ }
            }
        }
    }
    public int listenerCount() { return listeners.size(); }
}
