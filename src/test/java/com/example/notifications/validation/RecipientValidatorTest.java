package com.example.notifications.validation;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class RecipientValidatorTest {
 @Test void acceptsEmail() { assertDoesNotThrow(() -> RecipientValidator.validateEmail("a@b.com")); }
 @Test void rejectsEmail() { assertThrows(IllegalArgumentException.class, () -> RecipientValidator.validateEmail("invalid")); }
 @Test void acceptsPhone() { assertDoesNotThrow(() -> RecipientValidator.validatePhone("+54 11 1234-5678")); }
 @Test void rejectsPhone() { assertThrows(IllegalArgumentException.class, () -> RecipientValidator.validatePhone("abc")); }
}
