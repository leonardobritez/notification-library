package com.example.notifications.template;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Tiny {{variable}} template implementation, useful for user-defined templates. */
public class SimpleTextTemplate implements MessageTemplate {
    private static final Pattern TOKEN = Pattern.compile("\\{\\{\\s*([a-zA-Z0-9_.-]+)\\s*}} ".trim());
    private final String source;
    public SimpleTextTemplate(String source) {
        if (source == null || source.isBlank()) throw new IllegalArgumentException("template source is required");
        this.source = source;
    }
    @Override public String render(Map<String, Object> variables) {
        Matcher matcher = TOKEN.matcher(source);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            Object value = variables.get(matcher.group(1));
            if (value == null) throw new IllegalArgumentException("Missing required template variable: " + matcher.group(1));
            matcher.appendReplacement(result, Matcher.quoteReplacement(value.toString()));
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
