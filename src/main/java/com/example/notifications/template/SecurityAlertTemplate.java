package com.example.notifications.template;
import java.util.Map;
public class SecurityAlertTemplate implements MessageTemplate {
    @Override public String render(Map<String, Object> variables) {
        Object name = variables.get("name");
        if (name == null || name.toString().isBlank()) throw new IllegalArgumentException("Missing required template variable: name");
        return "Hola " + name + ", detectamos un inicio de sesión inusual.";
    }
}
