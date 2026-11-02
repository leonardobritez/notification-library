package com.example.notifications.template;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
class SimpleTextTemplateTest {
    @Test void replacesVariables() { assertEquals("Hola Leo, código 123", new SimpleTextTemplate("Hola {{name}}, código {{code}}").render(Map.of("name", "Leo", "code", 123))); }
    @Test void failsForMissingVariable() { assertThrows(IllegalArgumentException.class, () -> new SimpleTextTemplate("Hola {{name}}").render(Map.of())); }
}
