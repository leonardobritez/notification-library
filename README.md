# Notification Library (Java 17)

Librería de ejemplo para una prueba técnica: una API común para Email, SMS y Push; plantillas intercambiables; validación; resultado y eventos de estado mediante Observer; envío asíncrono con `CompletableFuture`; política simple de reintentos; configuración con clases Java y tests unitarios.

> **Importante:** Email/SMS/Push usan interfaces Gateway. No hay integración con proveedores reales ni se envían mensajes reales. La aplicación consumidora debe implementar `EmailGateway`, `SmsGateway` y `PushGateway` usando el proveedor elegido.

## Requisitos
- JDK 17 o superior
- Maven 3.9 o superior

## Ejecutar tests y compilar
```bash
mvn clean test
mvn package
```

## Cómo funciona
- `NotificationChannel`: contrato común; las implementaciones `EmailChannel`, `SmsChannel` y `PushChannel` validan el destinatario y delegan al gateway.
- `MessageTemplate`: estrategia para generar contenido. Se incluyen `WelcomeTemplate`, `SecurityAlertTemplate` y `SimpleTextTemplate` (sustitución de `{{variable}}`).
- `NotificationService`: genera un ID, publica `PENDING`, renderiza la plantilla, envía por el canal, y publica `SENT` o `FAILED`. `SENT` significa que el gateway/proveedor aceptó el envío; no garantiza que el destinatario lo haya recibido.
- `NotificationPublisher` / `NotificationListener`: Observer en el mismo proceso. Los errores de listeners se aíslan y no detienen a los demás listeners.
- `RetryPolicy`: `new RetryPolicy(3)` hace hasta tres intentos totales (incluyendo el primero); `RetryPolicy.noRetries()` hace uno.
- `sendAsync`: ejecuta el envío en el `Executor` configurado.

## Ejemplo de configuración Java
```java
EmailGateway email = (recipient, message) -> emailProvider.send(recipient, message);
SmsGateway sms = (recipient, message) -> smsProvider.send(recipient, message);
PushGateway push = (recipient, message) -> pushProvider.send(recipient, message);
NotificationService service = NotificationConfiguration.create(email, sms, push);
NotificationResult result = service.send(new NotificationRequest(
    ChannelType.EMAIL, "persona@dominio.com", "welcome", Map.of("name", "Ana")));
```

`NotificationConfiguration.create(...)` registra tres plantillas de ejemplo. Para agregar una plantilla, agregá una entrada al mapa `templates` o construí `NotificationService` con tu propio mapa.

## Registrar listeners propios
El ejemplo de `NotificationConfiguration` mantiene el publisher encapsulado para simplificar la creación. En una aplicación real, se recomienda crear y compartir el `NotificationPublisher` como parte de la configuración de dependencias y pasarlo al servicio y a los componentes que registran listeners:
```java
NotificationPublisher publisher = new NotificationPublisher();
publisher.subscribe(event -> {
    System.out.printf("id=%s canal=%s estado=%s instante=%s%n",
        event.notificationId(), event.channel(), event.status(), event.timestamp());
});
// Pasar esta misma instancia al constructor de NotificationService.
```
Un listener puede implementar auditoría, métricas o notificar a otra parte de la aplicación. No debe bloquear durante mucho tiempo; el publisher notifica de forma síncrona dentro del hilo que publica el evento.

## Errores y validación
- Email: validación sintáctica básica, no comprueba que el buzón exista.
- Teléfono: validación de formato básica, no reemplaza una librería de validación por país.
- Excepciones de proveedores se devuelven en `NotificationResult` como `FAILED` después de los intentos configurados.
- Los errores de plantilla/configuración también resultan en `FAILED`.

## Alcance / decisiones
- Observer está implementado dentro del mismo proceso. Kafka/RabbitMQ no se incluyen porque no son necesarios para este requisito de librería local; pueden agregarse detrás de una abstracción si se necesita distribución entre procesos.
- Los reintentos son inmediatos, sin backoff. Para producción, agregar backoff, límites, idempotencia y clasificación de errores transitorios/permanentes.
- `CompletableFuture` usa un `Executor` inyectado para evitar acoplar la librería al pool común global.
- Las plantillas de ejemplo son texto plano. Para HTML/localización se puede integrar Thymeleaf o FreeMarker manteniendo `MessageTemplate`.

## Estructura
```text
src/main/java/com/example/notifications/
  channel/       # contratos de canal y gateways de proveedores
  config/        # configuración Java
  event/         # NotificationEvent, publisher y listener
  exception/     # excepciones de dominio
  model/         # request, result y enums
  service/       # coordinación, async y retries
  template/      # interfaz y plantillas
  validation/    # validadores de destinatarios
src/test/java/   # tests unitarios con JUnit 5 y Mockito
```
