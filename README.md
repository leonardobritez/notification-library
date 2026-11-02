
<img width="1536" height="1024" alt="AdobeExpressPhotos_4922fd8499ef43d996615feca9f31fa8_CopyEdited" src="https://github.com/user-attachments/assets/058aed39-fb99-4b1b-8008-33867d461111" />

Conclusión
La arquitectura se diseñó para separar las responsabilidades de generación de mensajes, envío y publicación de eventos. Se utiliza Strategy para intercambiar los comportamientos de envío y generación de contenido, y Observer para comunicar los cambios de estado a los componentes interesados. Además, se aplican los principios de inversión de dependencias y composición sobre herencia para reducir el acoplamiento y facilitar la extensión y las pruebas del sistema.

<img width="964" height="465" alt="image" src="https://github.com/user-attachments/assets/64e46bec-96b3-4553-9539-7b4ee6c0393b" />

# 🔔 Notification Library
## 🏛️ Patrones de diseño y decisiones arquitectónicas

<p align="left">
  <img src="https://img.shields.io/badge/Java-17%2B-orange?logo=openjdk&logoColor=white" alt="Java 17+">
  <img src="https://img.shields.io/badge/Design%20Pattern-Strategy-blue" alt="Strategy">
  <img src="https://img.shields.io/badge/Design%20Pattern-Observer-purple" alt="Observer">
  <img src="https://img.shields.io/badge/Architecture-Loosely%20Coupled-success" alt="Loosely Coupled">
</p>

La librería de notificaciones se diseñó siguiendo principios de **bajo acoplamiento, alta cohesión y extensibilidad**, utilizando patrones de diseño que permiten incorporar nuevos canales, estrategias de generación de mensajes y mecanismos de observabilidad sin modificar innecesariamente la lógica principal.

Este documento describe los patrones y principios de diseño utilizados, su ubicación dentro de la arquitectura y las razones técnicas que justifican su elección.

---

## 📐 1. Strategy — Patrón Estrategia

![Strategy](https://img.shields.io/badge/Pattern-Strategy-2563EB?style=for-the-badge)

### 📍 ¿Dónde se aplica?

| Abstracción | Implementaciones |
|:---|:---|
| `NotificationChannel` | `EmailChannel`, `SmsChannel`, `PushChannel` |
| `MessageTemplate` | `WelcomeTemplate`, `SecurityAlertTemplate` y otras plantillas |

### 💡 ¿Por qué se aplicó?

El patrón **Strategy** permite definir una familia de algoritmos, encapsular cada uno en una implementación independiente e intercambiarlos sin modificar el código que los utiliza.

En esta librería se aplica en dos áreas diferentes.

#### ✉️ A. Estrategias de envío

Se define una interfaz común para todos los canales de notificación:

```java
public interface NotificationChannel {
    void send(String recipient, String message);
}
```

Cada canal implementa su propia lógica de envío:

- `EmailChannel`: envío de correos electrónicos.
- `SmsChannel`: envío de mensajes de texto.
- `PushChannel`: envío de notificaciones push.

`NotificationService` trabaja con la abstracción `NotificationChannel`, sin depender directamente de la implementación concreta del proveedor.

#### 📝 B. Estrategias de generación de mensajes

La interfaz `MessageTemplate` permite encapsular diferentes estrategias para generar el contenido de una notificación.

Por ejemplo:

- `WelcomeTemplate`: genera mensajes de bienvenida.
- `SecurityAlertTemplate`: genera alertas de seguridad.

Esto permite seleccionar o reemplazar una plantilla sin modificar la lógica del servicio que coordina el envío.

### ✅ Beneficios

- **Extensibilidad:** permite incorporar nuevos canales y plantillas.
- **Bajo acoplamiento:** separa la lógica de envío de la lógica de generación del mensaje.
- **Mantenibilidad:** cada implementación tiene una responsabilidad específica.
- **Reutilización:** permite combinar diferentes plantillas con diferentes canales.
- **Menor complejidad condicional:** evita centralizar toda la lógica en grandes estructuras `if/else` o `switch`.

> **Decisión arquitectónica:** encapsular los comportamientos variables detrás de interfaces permite que el sistema evolucione sin tener que modificar continuamente su lógica central.

---

## 📡 2. Observer — Patrón Observador

![Observer](https://img.shields.io/badge/Pattern-Observer-7C3AED?style=for-the-badge)

### 📍 ¿Dónde se aplica?

| Componente | Responsabilidad |
|:---|:---|
| `NotificationPublisher` | Publica eventos de notificación. |
| `NotificationListener` | Define el contrato para los suscriptores. |
| `NotificationEvent` | Representa un evento relacionado con una notificación. |
| `NotificationStatus` | Identifica el estado de la notificación. |

### 💡 ¿Por qué se aplicó?

El patrón **Observer** permite que un objeto notifique a múltiples suscriptores cuando ocurre un evento, sin necesidad de conocer los detalles de sus implementaciones.

En esta librería, `NotificationPublisher` publica eventos relacionados con el ciclo de vida de una notificación. Los componentes que implementan `NotificationListener` reciben esos eventos y pueden reaccionar de manera independiente.

Por ejemplo, cuando una notificación falla, se puede publicar un evento con estado `FAILED`.

Los distintos listeners pueden utilizar esa información para:

- 🧾 Registrar eventos de auditoría.
- 📊 Actualizar métricas.
- 🚨 Informar a sistemas de monitoreo.
- 🔎 Facilitar el seguimiento de errores.

El publicador no necesita conocer las funcionalidades concretas de cada listener.

### 🔄 Flujo conceptual

```text
             NotificationService
                      |
                      v
             NotificationPublisher
                      |
              NotificationEvent
                      |
           +----------+----------+
           |          |          |
           v          v          v
       Audit       Metrics    Monitoring
       Listener    Listener    Listener
```

*El diagrama representa el flujo conceptual del patrón; los listeners concretos dependen de las necesidades de cada aplicación.*

### ✅ Beneficios

- **Desacoplamiento:** el publicador no depende de las implementaciones de los suscriptores.
- **Extensibilidad:** se pueden registrar nuevos listeners sin modificar el servicio principal.
- **Separación de responsabilidades:** cada listener procesa los eventos según su propia finalidad.
- **Observabilidad:** facilita incorporar auditoría, métricas y monitoreo.

> **Nota arquitectónica:** esta implementación utiliza Observer dentro del mismo proceso Java. No requiere un broker externo como Apache Kafka o RabbitMQ. Si en el futuro fuera necesario distribuir los eventos entre diferentes procesos o servicios, podría incorporarse una infraestructura de mensajería mediante un adaptador específico.

---

## 🔗 3. Dependency Inversion Principle (DIP) — Inversión de dependencias

![SOLID](https://img.shields.io/badge/SOLID-Dependency%20Inversion-059669?style=for-the-badge)

> Este concepto es un principio de diseño de SOLID, no un patrón de diseño GoF.

### 📍 ¿Dónde se aplica?

- `NotificationService` utiliza la abstracción `NotificationChannel`.
- La generación del contenido se delega a la abstracción `MessageTemplate`.

### 💡 ¿Por qué se aplicó?

El principio de inversión de dependencias establece que los módulos de alto nivel no deberían depender directamente de los módulos de bajo nivel. Ambos deberían depender de abstracciones.

En esta arquitectura, el servicio principal no necesita conocer los detalles internos de cada proveedor de notificaciones.

Conceptualmente, trabaja con una dependencia como:

```java
private final NotificationChannel channel;
```

La implementación concreta se proporciona desde el exterior, por ejemplo mediante el constructor o una clase de configuración.

De esta manera, es posible sustituir un canal por otro siempre que ambos respeten el contrato definido por la interfaz.

### ✅ Beneficios

- **Menor acoplamiento:** el servicio no depende de proveedores concretos.
- **Testabilidad:** permite utilizar mocks o implementaciones simuladas en las pruebas unitarias.
- **Flexibilidad:** facilita integrar diferentes proveedores.
- **Mantenibilidad:** reduce el impacto de los cambios en las integraciones externas.

> **Decisión arquitectónica:** las abstracciones protegen la lógica central frente a los cambios en los detalles de infraestructura.

---

## 🧩 4. Composition over Inheritance — Composición sobre herencia

![Composition](https://img.shields.io/badge/Design%20Principle-Composition%20over%20Inheritance-DB2777?style=for-the-badge)

> Es un principio de diseño, no un patrón de diseño GoF.

### 📍 ¿Dónde se aplica?

En la colaboración entre:

- `NotificationService`
- `MessageTemplate`
- `NotificationChannel`

### 💡 ¿Por qué se aplicó?

La generación del contenido y el envío de una notificación son responsabilidades independientes.

Por ese motivo, no es necesario crear una jerarquía de clases para cada combinación posible de plantilla y canal.

Por ejemplo, evitamos una estructura como:

```text
EmailWelcomeTemplate
EmailSecurityAlertTemplate
SmsWelcomeTemplate
SmsSecurityAlertTemplate
PushWelcomeTemplate
PushSecurityAlertTemplate
```

En su lugar, combinamos implementaciones independientes:

```text
WelcomeTemplate       + EmailChannel
WelcomeTemplate       + SmsChannel
SecurityAlertTemplate + PushChannel
```

`NotificationService` coordina ambas abstracciones sin necesidad de heredar de ellas.

### ✅ Beneficios

- **Menos clases:** evita crear una clase por cada combinación de comportamientos.
- **Mayor flexibilidad:** permite combinar estrategias en tiempo de ejecución.
- **Menor acoplamiento:** los cambios en una plantilla no obligan a modificar los canales.
- **Reutilización:** las mismas plantillas pueden utilizarse con diferentes canales, siempre que el contenido sea compatible.

> **Decisión arquitectónica:** se favorece la composición de componentes independientes frente a jerarquías de herencia innecesariamente complejas.

---

## 📊 Resumen de patrones y principios

| Patrón / principio | Componentes principales | Objetivo |
|:---|:---|:---|
| **Strategy** | `NotificationChannel`, `MessageTemplate` | Intercambiar estrategias de envío y generación de mensajes. |
| **Observer** | `NotificationPublisher`, `NotificationListener` | Notificar eventos a múltiples suscriptores. |
| **Dependency Inversion (DIP)** | `NotificationService` y sus interfaces | Reducir la dependencia de implementaciones concretas. |
| **Composition over Inheritance** | `NotificationService`, `MessageTemplate`, `NotificationChannel` | Combinar comportamientos sin proliferación de clases. |

---

## 🏁 Conclusión

La arquitectura propuesta separa las responsabilidades de generación de mensajes, envío y publicación de eventos para facilitar la evolución de la librería.

La aplicación de **Strategy** permite intercambiar los comportamientos de envío y generación de contenido. **Observer** proporciona un mecanismo extensible para comunicar los cambios de estado a los componentes interesados. Por su parte, los principios de **inversión de dependencias** y **composición sobre herencia** contribuyen a reducir el acoplamiento y facilitar las pruebas unitarias.

El resultado es una arquitectura modular que permite incorporar nuevas funcionalidades con un impacto reducido sobre los componentes existentes.

**Objetivo de diseño:** una librería fácil de extender, mantener y probar, con responsabilidades claramente separadas y dependencias orientadas a abstracciones.









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
