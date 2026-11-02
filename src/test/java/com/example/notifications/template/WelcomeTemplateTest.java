package com.example.notifications.template;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
class WelcomeTemplateTest {
    @Test void rendersName() { assertEquals("Hola Leo, ¡bienvenido!", new WelcomeTemplate().render(Map.of("name", "Leo"))); }
    @Test void requiresName() { assertThrows(IllegalArgumentException.class, () -> new WelcomeTemplate().render(Map.of())); }
}
