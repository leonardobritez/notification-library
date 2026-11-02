package com.example.notifications.validation;
import java.util.regex.Pattern;
public final class RecipientValidator {
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern PHONE = Pattern.compile("^\\+?[0-9][0-9 ()-]{6,19}$");
    private RecipientValidator() { }
    public static void validateEmail(String email) {
        if (email == null || !EMAIL.matcher(email).matches()) throw new IllegalArgumentException("Invalid email address");
    }
    public static void validatePhone(String phone) {
        if (phone == null || !PHONE.matcher(phone).matches()) throw new IllegalArgumentException("Invalid phone number");
    }
}
